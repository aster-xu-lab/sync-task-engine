package com.cloud.sync.task.engine.spi;

import java.util.List;

/**
 * 任务存储抽象（SPI）。
 *
 * <p>引擎通过此接口操作任务数据，不绑定任何 ORM（MyBatis/JPA/Mongo）。
 * 接入方负责实现具体的 CRUD 逻辑。</p>
 *
 * <p>这是 SyncTaskJob 中直接依赖 ISyncTaskService 的替代方案——
 * 原来 Handler 基类通过 @Resource 注入 ISyncTaskService，
 * 现在由引擎通过此接口统一操作存储。</p>
 *
 * @param <T> 任务业务对象类型（各项目自定义的 BO）
 * @param <Q> 任务查询条件类型
 * @param <P> 分页参数类型
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public interface TaskStore<T, Q, P> {

    /**
     * 根据 ID 查询单条任务。
     */
    T findById(Long id);

    /**
     * 动态查询所有符合条件的任务。
     */
    List<T> findAll(Q query);

    /**
     * 分页查询任务。
     */
    List<T> pageList(Q query, P pageParam);

    /**
     * 获取分页查询的总条数（可选，返回 null 表示不支持）。
     */
    default Long count(Q query) {
        return null;
    }

    /**
     * 动态更新任务状态。
     *
     * @param param 要更新的字段
     * @param query 更新条件
     */
    void update(Object param, Q query);

    /**
     * 根据 ID 更新单条任务。
     */
    void updateById(Object param);

    /**
     * 批量根据 ID 更新任务。
     */
    void batchUpdateById(List<?> params);

    /**
     * 递增指定任务的重试次数。
     */
    void incrementRetryTimes(List<Long> ids);
}
