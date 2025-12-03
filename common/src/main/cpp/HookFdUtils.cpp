#include <jni.h>
#include <string>
#include <sys/mman.h>
#include <sys/eventfd.h>
#include <unistd.h>
#include <android/log.h>
#include <thread>
#include <sys/epoll.h>
#include <atomic>
#include <vector>

#define TAG "IPC_Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// 每个数据包的头部 Magic，用于校验数据边界有效性
#define PACKET_MAGIC 0xAABBCCDD

struct SharedHeader {
    // 读写指针
    std::atomic<uint32_t> write_pos;
    // Seqlock 版本号 (核心机制)
    // 偶数：空闲/稳定状态
    // 奇数：正在写入/不稳定状态
    std::atomic<uint64_t> seqlock;

    uint32_t data_size;
    uint32_t header_magic; // 全局 Magic
};

// 全局变量保存映射的地址
uint8_t* g_shared_mem_addr = nullptr;
SharedHeader* g_header = nullptr;
uint8_t* g_data_buffer = nullptr;
int g_buffer_capacity = 0;
bool g_is_running = false;

extern "C" JNIEXPORT void JNICALL
Java_com_android_common_LibIPC_nativeInit(JNIEnv* env, jobject, jint fd, jint size, jboolean isProducer) {
    // 1. 内存映射 (MMAP)
    // PROT_READ | PROT_WRITE 允许读写
    // MAP_SHARED 使得修改对其他进程可见
    g_shared_mem_addr = (uint8_t*)mmap(nullptr, size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);

    if (g_shared_mem_addr == MAP_FAILED) {
        LOGE("mmap failed!");
        return;
    }

    // 2. 初始化指针
    g_header = (SharedHeader*)g_shared_mem_addr;
    g_data_buffer = g_shared_mem_addr + sizeof(SharedHeader);
    g_buffer_capacity = size - sizeof(SharedHeader);

    if (isProducer) {
        // 初始化
        g_header->write_pos = 0;
        g_header->seqlock = 0; // 初始版本号 0
        g_header->data_size = g_buffer_capacity;
        g_header->header_magic = 0x12345678;
        LOGI("Producer init done. Capacity: %d", g_buffer_capacity);
    } else {
        LOGI("Consumer init done. Magic: %x", g_header->header_magic);
    }
}

// 创建 eventfd 用于通知 (替代 socketpair，功能类似但更轻量)
extern "C" JNIEXPORT jint JNICALL
Java_com_android_common_LibIPC_nativeCreateEventFd(JNIEnv* env, jobject) {
    return eventfd(0, EFD_NONBLOCK | EFD_CLOEXEC);
}

// 生产者：写入数据并发送通知
extern "C" JNIEXPORT void JNICALL
Java_com_android_common_LibIPC_nativeWriteData(JNIEnv* env, jobject, jint eventFd, jstring dataStr) {
    if (!g_data_buffer) return;

    const char* chars = env->GetStringUTFChars(dataStr, nullptr);
    int payload_len = env->GetStringUTFLength(dataStr);

    // Packet 总长 = Magic(4) + Len(4) + Data
    int packet_total_len = 4 + 4 + payload_len;

    // --- 1. 开启 Seqlock 事务 (版本号 +1，变成奇数) ---
    // memory_order_acquire 确保之前的读写全部完成
    uint64_t current_seq = g_header->seqlock.load(std::memory_order_relaxed);
    g_header->seqlock.store(current_seq + 1, std::memory_order_release);

    // --- 2. 计算写入位置 ---
    uint32_t current_write = g_header->write_pos.load(std::memory_order_relaxed);

    // 检查是否需要回绕 (Padding Protocol)
    // 如果剩余空间放不下这个包，就在当前位置写 0 (Padding)，然后回绕
    bool wrapped = false;
    if (current_write + packet_total_len > g_buffer_capacity) {
        // 只有当剩余空间足够写一个 Length(4) 时才写 Padding 0
        // 否则直接回绕即可，因为消费者会有边界检查
        if (g_buffer_capacity - current_write >= 4) {
            // 写入长度 0，表示 Padding
            *(uint32_t*)(g_data_buffer + current_write + 4) = 0;
        }
        current_write = 0;
        wrapped = true;
    }

    // --- 3. 强行写入数据 (Overwrite) ---
    // 不管读指针在哪里，直接写。Seqlock 会保证消费者能发现数据变脏了。

    // 写入 Packet Magic
    *(uint32_t*)(g_data_buffer + current_write) = PACKET_MAGIC;
    // 写入 Length
    *(uint32_t*)(g_data_buffer + current_write + 4) = payload_len;
    // 写入 Body
    memcpy(g_data_buffer + current_write + 8, chars, payload_len);

    // 更新写指针
    uint32_t new_write_pos = current_write + packet_total_len;
    g_header->write_pos.store(new_write_pos, std::memory_order_relaxed);

    // --- 4. 结束 Seqlock 事务 (版本号 +1，变回偶数) ---
    // memory_order_release 确保数据真正落盘后，版本号才更新
    g_header->seqlock.store(current_seq + 2, std::memory_order_release);

    env->ReleaseStringUTFChars(dataStr, chars);

    // 通知
    uint64_t u = 1;
    write(eventFd, &u, sizeof(uint64_t));
}

// 消费者：启动 Epoll 监听
extern "C" JNIEXPORT void JNICALL
Java_com_android_common_LibIPC_nativeStartListen(JNIEnv* env, jobject thiz, jint eventFd) {
    g_is_running = true;

    // --- 关键修复 1: 创建 Global Reference ---
    // thiz 是局部引用，不能跨线程，必须转为全局引用
    jobject globalThiz = env->NewGlobalRef(thiz);

    // 获取方法ID (MethodID 可以在多线程共享，只要类不被卸载)
    jclass clazz = env->GetObjectClass(thiz);
    jmethodID onDataRecv = env->GetMethodID(clazz, "onDataReceived", "(Ljava/lang/String;)V");

    JavaVM* jvm;
    env->GetJavaVM(&jvm);

    std::thread([=]() {
        JNIEnv* threadEnv;
        // Attach 当前线程到 JVM，获取 threadEnv
        if (jvm->AttachCurrentThread(&threadEnv, nullptr) != JNI_OK) return;

        int epollFd = epoll_create1(0);
        struct epoll_event ev, events[1];
        ev.events = EPOLLIN;
        ev.data.fd = eventFd;
        epoll_ctl(epollFd, EPOLL_CTL_ADD, eventFd, &ev);

        uint32_t local_read_pos = 0;

        while (g_is_running) {
            int nfds = epoll_wait(epollFd, events, 1, -1);
            if (nfds > 0) {
                uint64_t u;
                read(eventFd, &u, sizeof(uint64_t));

                uint32_t server_write_pos = g_header->write_pos.load(std::memory_order_acquire);
                while (local_read_pos != server_write_pos) {
                    if (local_read_pos + 4 > g_buffer_capacity) local_read_pos = 0;
                    uint32_t msgLen = *(uint32_t*)(g_data_buffer + local_read_pos);
                    local_read_pos += 4;

                    char buffer[1024] = {0};
                    if (msgLen < 1024) {
                        memcpy(buffer, g_data_buffer + local_read_pos, msgLen);
                        local_read_pos += msgLen;

                        jstring jStr = threadEnv->NewStringUTF(buffer);

                        // --- 关键修复 2: 使用 Global Reference 进行回调 ---
                        threadEnv->CallVoidMethod(globalThiz, onDataRecv, jStr);

                        threadEnv->DeleteLocalRef(jStr);
                    } else {
                        local_read_pos = server_write_pos;
                    }
                }
            }
        }

        // --- 关键修复 3: 释放 Global Reference ---
        // 线程结束时，必须释放全局引用，否则内存泄漏
        threadEnv->DeleteGlobalRef(globalThiz);

        close(epollFd);
        jvm->DetachCurrentThread();
    }).detach();
}