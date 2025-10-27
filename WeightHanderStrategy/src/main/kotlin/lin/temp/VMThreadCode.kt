package lin.temp

/**
 * 一.runContinuation是载体线程执行虚拟线程任务的入口
 * [VirtualThread.runContinuation]
 * 2.finally里判断任务是否是完成或者挂起,挂起会添加定时任务
 * a.runContinuation的finally会判断任务是否完成
 * [jdk.internal.vm.Continuation.isDone]
 * 1a.添加定时任务位置
 * [VirtualThread.afterYield]
 *
 *
 *
 * 二.park只负责切换上下文(具体yieldContinuation)
 * [VirtualThread.parkNanos]
 * 1.yieldContinuation在Continuation里标记未完成
 * [VirtualThread.yieldContinuation]
 *
 *
 */
class VMThreadCode