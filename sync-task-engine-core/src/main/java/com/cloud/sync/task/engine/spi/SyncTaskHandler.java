package com.cloud.sync.task.engine.spi;

import java.util.List;

/**
 * 同步任务处理器接口。
 * <p>引擎通过此接口与业务 Handler 交互。接入方 Handler 需实现此接口并在 Spring 容器中注册。</p>
 *
 * @param <T> 任务业务对象类型（各项目自定义）
 * @param <C> 任务上下文类型（各项目自定义）
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public interface SyncTaskHandler<T, C> {

    /**
     * 获取处理器唯一 key。
     * <p>格式由 Handler 自行决定，引擎不做预设。
     * 例如 bizType-syncTaskType-syncSystem，格式由接入方自行约定。</p>
     *
     * @return Handler key
     */
    String getHandlerKey();

    /**
     * 从数据库拉取待处理的同步任务。
     *
     * @param param 任务参数
     * @return 待处理任务列表
     */
    List<T> fetchTasks(SyncTaskParam param);

    /**
     * 批量执行同步任务。
     *
     * @param tasks 待处理任务列表
     * @param param 任务参数
     * @throws InterruptedException 并发中断
     */
    void batchExecute(List<T> tasks, SyncTaskParam param) throws InterruptedException;

    /**
     * 处理单条同步任务。
     *
     * @param context 任务上下文
     * @param param 任务参数
     * @return true-成功，false-失败
     */
    boolean execute(C context, SyncTaskParam param);

    /**
     * 处理前置逻辑。
     *
     * @param context 任务上下文
     */
    default void beforeExecute(C context) {}

    /**
     * 处理后置逻辑（仅 execute 返回 true 时调用）。
     *
     * @param context 任务上下文
     */
    default void afterExecute(C context) {}

    /**
     * 获取企微机器人 key。
     *
     * @return 机器人 key
     */
    default String getQWRobotKey() {
        return this.getClass().getSimpleName();
    }

    /**
     * 格式化错误信息为告警消息。
     *
     * @param context 任务上下文
     * @param message 原始错误信息
     * @return 格式化后的告警消息
     */
    String formatMessage(C context, String message);
}
