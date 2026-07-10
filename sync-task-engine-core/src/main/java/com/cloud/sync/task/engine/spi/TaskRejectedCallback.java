package com.cloud.sync.task.engine.spi;

/**
 * 拒绝策略回调接口。
 * <p>线程池队列满时，被拒绝的任务通过此回调通知接入方。
 * 接入方负责将对应任务标记为失败状态，并递减 CountDownLatch 防止死锁。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
@FunctionalInterface
public interface TaskRejectedCallback {

    /**
     * 当任务被线程池拒绝时回调。
     *
     * @param taskId        被拒绝的任务 ID
     * @param rejectReason  拒绝原因
     */
    void onTaskRejected(Long taskId, String rejectReason);
}
