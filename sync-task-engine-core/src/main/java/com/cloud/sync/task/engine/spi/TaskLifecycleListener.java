package com.cloud.sync.task.engine.spi;

import java.util.List;

/**
 * 任务生命周期事件监听器（SPI）。
 *
 * <p>引擎在每个关键节点触发事件回调，接入方可注册多个 Listener 实现：
 * 日志记录、Metrics 埋点、审计追踪、后续编排等。</p>
 *
 * <p>所有方法都有默认空实现，接入方按需覆盖。各 Listener 之间互相独立，
 * 一个抛异常不影响其他 Listener 的执行。</p>
 *
 * <p>这是 SyncTaskJob 中 afterExecute 埋点编排的升级版——
 * 原来 Handler 在 afterExecute 里手动创建下一跳 sync_task 实现编排链，
 * 现在可以通过 onTaskSuccess 事件统一触发后续流程，
 * 或者继续用 afterExecute，二者不冲突。</p>
 *
 * @param <T> 任务业务对象类型
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public interface TaskLifecycleListener<T> {

    /** 一批任务拉取完成后 */
    default void onTasksFetched(List<T> tasks, SyncTaskParam config) {}

    /** 单条任务执行成功后 */
    default void onTaskSuccess(T task, SyncTaskParam config) {}

    /** 单条任务执行失败后 */
    default void onTaskFailed(T task, String errorMessage, SyncTaskParam config) {}

    /** 单条任务被取消后 */
    default void onTaskCancelled(T task, String reason, SyncTaskParam config) {}

    /** 单条任务进入等待后 */
    default void onTaskWaiting(T task, String reason, SyncTaskParam config) {}

    /** 整批任务执行完成后 */
    default void onBatchCompleted(List<T> successTasks, List<T> failedTasks, SyncTaskParam config) {}

    /** 告警触发后 */
    default void onAlertTriggered(List<T> failedTasks, NotifyChannel.NotifyLevel level, SyncTaskParam config) {}

    /** 有任务被拒绝（线程池满） */
    default void onTasksRejected(List<Long> taskIds, String reason, SyncTaskParam config) {}
}
