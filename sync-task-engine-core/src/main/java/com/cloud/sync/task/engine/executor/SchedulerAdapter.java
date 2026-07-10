package com.cloud.sync.task.engine.executor;

/**
 * 调度框架适配器接口。
 * <p>引擎通过此接口与具体的调度框架（xxl-job / PowerJob / 手动触发）解耦。
 * 接入方负责实现此接口以适配自己的调度框架。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public interface SchedulerAdapter {

    /**
     * 从当前线程上下文中提取调度参数（JSON 字符串）。
     * <p>xxl-job 实现从 XxlJobHelper.getJobParam() 获取，
     * PowerJob 实现从 TaskContext 获取，手动触发直接传入。</p>
     *
     * @return 调度原始参数（JSON 字符串）
     */
    String resolveJobParam();

    /**
     * 向调度平台的日志通道输出信息日志。
     *
     * @param pattern 日志格式
     * @param args    参数
     */
    void logInfo(String pattern, Object... args);

    /**
     * 向调度平台的日志通道输出错误日志。
     *
     * @param pattern 日志格式
     * @param args    参数
     */
    void logError(String pattern, Object... args);
}
