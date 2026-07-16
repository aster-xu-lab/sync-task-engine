package com.cloud.sync.task.engine.adapter.mybatis;

import com.cloud.sync.task.engine.spi.TaskArchiveService;

import java.util.Date;

/**
 * MyBatis 任务归档默认实现（空实现）。
 * <p>接入方需通过子类覆写实现具体的归档 mapper 调用。</p>
 *
 * @param <T> 任务业务对象类型
 *
 * @author sync-task-engine
 * @date 2026-07-15
 */
public class MybatisTaskArchiveService<T> implements TaskArchiveService<T> {

    @Override
    public int archiveSuccessTasks(Date beforeTime) {
        throw new UnsupportedOperationException("请通过子类覆写 archiveSuccessTasks 实现具体 mapper 调用");
    }

    @Override
    public int archiveDeadTasks(Date beforeTime) {
        throw new UnsupportedOperationException("请通过子类覆写 archiveDeadTasks 实现具体 mapper 调用");
    }
}
