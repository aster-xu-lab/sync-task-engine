package com.cloud.sync.task.engine.adapter.mybatis;

import com.cloud.sync.task.engine.spi.TaskStore;

import java.util.List;

/**
 * MyBatis-Plus 任务存储默认实现。
 * <p>基于 MyBatis-Plus 的通用 CRUD 操作封装。
 * 接入方需通过子类覆写各方法实现具体 mapper 调用。</p>
 *
 * @param <T> 任务业务对象类型
 *
 * @author sync-task-engine
 * @date 2026-07-15
 */
public class MybatisTaskStore<T> implements TaskStore<T, Object, Object> {

    @Override
    public T findById(Long id) {
        throw new UnsupportedOperationException("请通过子类覆写 findById 实现具体 mapper 调用");
    }

    @Override
    public List<T> findAll(Object query) {
        throw new UnsupportedOperationException("请通过子类覆写 findAll 实现具体 mapper 调用");
    }

    @Override
    public List<T> pageList(Object query, Object pageParam) {
        throw new UnsupportedOperationException("请通过子类覆写 pageList 实现具体 mapper 调用");
    }

    @Override
    public void update(Object param, Object query) {
        throw new UnsupportedOperationException("请通过子类覆写 update 实现具体 mapper 调用");
    }

    @Override
    public void updateById(Object param) {
        throw new UnsupportedOperationException("请通过子类覆写 updateById 实现具体 mapper 调用");
    }

    @Override
    public void batchUpdateById(List<?> params) {
        throw new UnsupportedOperationException("请通过子类覆写 batchUpdateById 实现具体 mapper 调用");
    }

    @Override
    public void incrementRetryTimes(List<Long> ids) {
        throw new UnsupportedOperationException("请通过子类覆写 incrementRetryTimes 实现具体 mapper 调用");
    }
}
