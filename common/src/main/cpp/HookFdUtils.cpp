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

// 共享内存头部结构 (16 bytes)
struct SharedHeader {
    std::atomic<uint32_t> write_pos; // 写索引
    std::atomic<uint32_t> read_pos;  // 读索引 (Client 维护，简单 demo 中我们只让 Server 覆盖写，Client 追赶)
    uint32_t data_size;              // 缓冲区总大小
    uint32_t magic;                  // 校验位
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
        // 生产者初始化 Header
        g_header->write_pos = 0;
        g_header->read_pos = 0;
        g_header->data_size = g_buffer_capacity;
        g_header->magic = 0x12345678;
        LOGI("Producer init done. Capacity: %d", g_buffer_capacity);
    } else {
        LOGI("Consumer init done. Magic: %x", g_header->magic);
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
    int len = env->GetStringUTFLength(dataStr);

    // 获取当前写位置 (Relaxed 即可，因为只有一个 Producer 线程)
    uint32_t current_write = g_header->write_pos.load(std::memory_order_relaxed);
    bool need_wrap = false;

    // --- 核心修复：回绕判断与填充逻辑 ---

    // 情况1: 连头部 4 字节都写不下了
    if (current_write + 4 > g_buffer_capacity) {
        need_wrap = true;
    }
        // 情况2: 头部能写下，但数据写不下
    else if (current_write + 4 + len > g_buffer_capacity) {
        // 关键：在这里写入 0 作为 Padding，告诉消费者“此路不通，去头部”
        *(uint32_t*)(g_data_buffer + current_write) = 0;
        need_wrap = true;
    }

    if (need_wrap) {
        current_write = 0;
    }

    // --- 写入新数据 ---

    // 再次检查头部（防止 capacity 极小的情况，防御性编程）
    if (current_write + 4 <= g_buffer_capacity) {
        *(uint32_t*)(g_data_buffer + current_write) = len;
        current_write += 4;
    }

    // 写入 Payload
    if (current_write + len <= g_buffer_capacity) {
        memcpy(g_data_buffer + current_write, chars, len);
        current_write += len;
    }

    // 发布写指针 (Release 语义，保证数据先落盘)
    g_header->write_pos.store(current_write, std::memory_order_release);

    env->ReleaseStringUTFChars(dataStr, chars);

    // 通知 EventFD
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