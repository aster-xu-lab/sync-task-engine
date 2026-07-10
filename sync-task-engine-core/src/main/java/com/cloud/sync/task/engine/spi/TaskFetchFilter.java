package com.cloud.sync.task.engine.spi;

import java.util.List;

/**
 * 任务拉取过滤器（SPI）。
 *
 * <p>在从 TaskStore 拉取任务后、执行前，对任务列表做二次过滤。
 * 用于实现业务层面的过滤规则，如排除黑名单商户、节假日跳过等。</p>
 *
 * @param <T> 任务业务对象类型
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public interface TaskFetchFilter<T> {

    /**
     * 过滤任务列表。
     *
     * @param tasks  拉取到的原始任务列表
     * @param param  当前执行参数
     * @return 过滤后的任务列表
     */
    List<T> filter(List<T> tasks, SyncTaskParam param);

    /**
     * 执行顺序，数字越小越先执行。
     */
    default int getOrder() {
        return 0;
    }
}
