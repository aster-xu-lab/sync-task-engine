package com.cloud.sync.task.engine.adapter.mybatis;

import com.cloud.sync.task.engine.spi.SyncTaskParam;
import com.cloud.sync.task.engine.spi.TaskStore;

import java.util.List;

/**
 * MyBatis-Plus 任务存储默认实现。
 * <p>基于 MyBatis-Plus 的通用 CRUD 操作封装。</p>
 *
 * @param <T> 任务业务对象类型
 *
 * @author sync-task-engine
 * @date 2026-07-15
 */
public class MybatisTaskStore<T> implements TaskStore<T, Object, Object> {

    /**
     * 查找单个任务。接入方需通过子类覆写实现具体 mapper 调用。
     */
    @Override
    public T findById(Long id) {
        throw new UnsupportedOperationException("请通过子类覆写 findById 实现具体 mapper 调用");
    }

    /**
     * 动态查询所有符合条件的任务。接入方需通过子类覆写实现具体 mapper 调用。
     */
    @Override
    public List<T> findAll(Object query) {
        throw new UnsupportedOperationException("请通过子类覆写 findAll 实现具体 mapper 调用");
    }

    /**
     * 分页查询任务。接入方需通过子类覆写实现具体 mapper 调用。
     */
    @Override
    public List<T> pageList(Object query, Object pageParam) {
        throw new UnsupportedOperationException("请通过子类覆写 pageList 实现具体 mapper 调用");
    }

    /**
     * 动态更新任务。接入方需通过子类覆写实现具体 mapper 调用。
     */
    @Override
    public void update(Object param, Object query) {
        throw new UnsupportedOperationException("请通过子类覆写 update 实现具体 mapper 调用");
    }

    /**
     * 根据 ID 更新单条任务。接入方需通过子类覆写实现具体 mapper 调用。
     */
    @Override
    public void updateById(Object param) {
        throw new UnsupportedOperationException("请通过子类覆写 updateById 实现具体 mapper 调用");
    }

    /**
     * 批量更新任务。接入方需通过子类覆写实现具体 mapper 调用。
     */
    @Override
    public void batchUpdateById(List<?> params) {
        throw new UnsupportedOperationException("请通过子类覆写 batchUpdateById 实现具体 mapper 调用");
    }

    /**
     * 递增重试次数。接入方需通过子类覆写实现具体 mapper 调用。
     */
    @Override
    public void incrementRetryTimes(List<Long> ids) {
        throw new UnsupportedOperationException("请通过子类覆写 incrementRetryTimes 实现具体 mapper 调用");
    }
}
