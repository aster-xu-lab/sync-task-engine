package com.cloud.sync.task.engine.adapter.mybatis;

import com.cloud.sync.task.engine.adapter.mybatis.mapper.SyncTaskMapper;
import com.cloud.sync.task.engine.spi.TaskArchiveService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * MyBatis 任务归档默认实现。
 * <p>基于标准表结构通过 INSERT INTO ... SELECT 将数据移入归档表。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-16
 */
public class MybatisTaskArchiveService implements TaskArchiveService<Object> {

    private final SyncTaskMapper syncTaskMapper;

    public MybatisTaskArchiveService(SyncTaskMapper syncTaskMapper) {
        this.syncTaskMapper = syncTaskMapper;
    }

    @Override
    public int archiveSuccessTasks(Date beforeTime) {
        LocalDateTime before = toLocalDateTime(beforeTime);
        int count = syncTaskMapper.archiveSuccessTasks(before);
        // 归档后删除原表已归档数据
        if (count > 0) {
            deleteArchivedSuccess(before);
        }
        return count;
    }

    @Override
    public int archiveDeadTasks(Date beforeTime) {
        LocalDateTime before = toLocalDateTime(beforeTime);
        int count = syncTaskMapper.archiveDeadTasks(before);
        // 归档后删除原表已归档数据
        if (count > 0) {
            deleteArchivedDead(before);
        }
        return count;
    }

    private void deleteArchivedSuccess(LocalDateTime beforeTime) {
        syncTaskMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.cloud.sync.task.engine.adapter.mybatis.entity.SyncTaskDO>()
                .eq(com.cloud.sync.task.engine.adapter.mybatis.entity.SyncTaskDO::getTaskStatus, "SUCCESS")
                .le(com.cloud.sync.task.engine.adapter.mybatis.entity.SyncTaskDO::getUpdateTime, beforeTime));
    }

    private void deleteArchivedDead(LocalDateTime beforeTime) {
        syncTaskMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.cloud.sync.task.engine.adapter.mybatis.entity.SyncTaskDO>()
                .eq(com.cloud.sync.task.engine.adapter.mybatis.entity.SyncTaskDO::getTaskStatus, "FAIL")
                .le(com.cloud.sync.task.engine.adapter.mybatis.entity.SyncTaskDO::getUpdateTime, beforeTime));
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
