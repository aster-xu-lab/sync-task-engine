package com.cloud.sync.task.engine.adapter.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.sync.task.engine.adapter.mybatis.entity.SyncTaskDO;
import com.cloud.sync.task.engine.adapter.mybatis.mapper.SyncTaskMapper;
import com.cloud.sync.task.engine.spi.TaskStore;

import java.util.Arrays;
import java.util.List;

/**
 * MyBatis-Plus 任务存储默认实现。
 * <p>基于标准表结构 {@link SyncTaskDO} 提供开箱即用的 CRUD 操作。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-16
 */
public class MybatisTaskStore implements TaskStore<SyncTaskDO, LambdaQueryWrapper<SyncTaskDO>, Page<SyncTaskDO>> {

    private final SyncTaskMapper syncTaskMapper;

    /** 默认拉取的状态列表 */
    private static final List<String> DEFAULT_PENDING_STATUSES = Arrays.asList("INIT", "FAIL", "WAIT");

    public MybatisTaskStore(SyncTaskMapper syncTaskMapper) {
        this.syncTaskMapper = syncTaskMapper;
    }

    @Override
    public SyncTaskDO findById(Long id) {
        return syncTaskMapper.selectById(id);
    }

    @Override
    public List<SyncTaskDO> findAll(LambdaQueryWrapper<SyncTaskDO> query) {
        return syncTaskMapper.selectList(query);
    }

    @Override
    public List<SyncTaskDO> pageList(LambdaQueryWrapper<SyncTaskDO> query, Page<SyncTaskDO> pageParam) {
        return syncTaskMapper.selectPage(pageParam, query).getRecords();
    }

    @Override
    public Long count(LambdaQueryWrapper<SyncTaskDO> query) {
        return syncTaskMapper.selectCount(query);
    }

    @Override
    public void update(Object param, LambdaQueryWrapper<SyncTaskDO> query) {
        if (param instanceof LambdaUpdateWrapper) {
            syncTaskMapper.update(null, (LambdaUpdateWrapper<SyncTaskDO>) param);
        } else {
            throw new IllegalArgumentException("param 必须是 LambdaUpdateWrapper<SyncTaskDO> 类型");
        }
    }

    @Override
    public void updateById(Object param) {
        if (param instanceof SyncTaskDO) {
            syncTaskMapper.updateById((SyncTaskDO) param);
        } else {
            throw new IllegalArgumentException("param 必须是 SyncTaskDO 类型");
        }
    }

    @Override
    public void batchUpdateById(List<?> params) {
        for (Object param : params) {
            if (param instanceof SyncTaskDO) {
                syncTaskMapper.updateById((SyncTaskDO) param);
            }
        }
    }

    @Override
    public void incrementRetryTimes(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            syncTaskMapper.incrementRetryTimes(ids);
        }
    }

    /**
     * 按默认状态列表拉取待处理任务。
     */
    public List<SyncTaskDO> fetchPendingTasks(int limit) {
        return syncTaskMapper.selectPendingTasks(DEFAULT_PENDING_STATUSES, limit);
    }

    /**
     * 按指定状态列表拉取待处理任务。
     */
    public List<SyncTaskDO> fetchPendingTasks(List<String> statuses, int limit) {
        return syncTaskMapper.selectPendingTasks(statuses, limit);
    }
}
