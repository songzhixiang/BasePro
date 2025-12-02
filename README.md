# BasePro

[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=33)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)




### 进程间通讯的几种方式
控制面（Control Plane）：使用 Binder。只在初始化时运行一次，传递 FD（文件描述符）。这非常轻量。

数据面（Data Plane）：
1. 共享内存 (SharedMemory / FMQ) 的机制，用于绕过 Binder 的数据拷贝。
2. Binder


同步面（Sync Plane）：使用 socketpair。解决“什么时候读”的问题。

1. socketpair 的优点
全双工：如果你需要 Client 收到数据后，反向告诉 Service “我读完了，你可以写新数据了”（Flow Control），socketpair 天然支持双向通信。
Looper 集成：在 Android Native 开发中，socketpair 的 FD 可以完美集成到 Looper (epoll) 机制中 (SimpleLooper / ALooper)。
当 Service 写入一个字节时，Client 端的 Looper 会自动唤醒回调，不需要你手动写 while(true) 去轮询，省电且优雅。
跨进程能力：天生支持通过 Binder 传递 FD。

socketpair 是全双工的，如果你需要 App 收到数据后回复 Service "我收到了"，那么用 socketpair 会更好。现在的代码是单向通知。
如果你非要用 socketpair，只需将 eventfd(0,0) 替换为 socketpair(AF_UNIX, SOCK_STREAM, 0, fds)，然后把 fds[0] 给 Service 写，fds[1] 传给 App 读即可，epoll 逻辑完全不用变。

实际案例
Android 的 InputFlinger（输入系统）早期版本和现在的部分逻辑中，Window 与 InputDispatcher 之间的通信通道（InputChannel），其底层本质上就是一对 socketpair（现在逐渐转向 BitTube 甚至更底层的实现，但原理一致）。
它利用 socket 发送输入事件，利用共享内存（Ashmem）共享光标图形等大数据。

2. 使用 eventfd 替代 socketpair
如果你的通知仅仅是“唤醒对方”，不需要传递具体的控制指令（比如只是告诉对方有数据了），eventfd 是 Linux 专门为此设计的轻量级计数器。
开销更小：eventfd 只是内核里的一个 64 位计数器，比 socket 对象轻得多。
用法一样：同样可以被 epoll 监听，同样可以通过 Binder 传递 FD。
场景：Service 往 eventfd write 1，Client 被唤醒读取。

3. MessageQueue.addOnFileDescriptorEventListener


### FMQ 是基于 Android HIDL/AIDL 提供的 无锁环形缓冲区。
Android 官方为了解决你提到的这个问题（特别是 Audio HAL 和 Sensor HAL 的高频数据传输），在 HIDL/AIDL 中引入了 FMQ (Fast Message Queue) 库。

FMQ 的底层实现逻辑与你的一模一样，但它做到了极致：

共享内存：使用 MessageQueue 结构。

同步机制：使用 Futex (User-space locking) 配合 EventFlag（底层可能是 eventfd 或驱动支持的等待队列）。

特性：

如果是非阻塞模式，Client 甚至不需要等待通知，直接原子操作检查共享内存标志位（Polling 模式），速度极快（纳秒级）。

如果是阻塞模式，则回退到类似你说的 wait/wake 机制。


### Ring Buffer
https://zhuanlan.zhihu.com/p/714495196