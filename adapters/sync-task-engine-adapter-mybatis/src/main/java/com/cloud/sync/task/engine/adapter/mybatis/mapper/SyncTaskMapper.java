package com.cloud.sync.task.engine.adapter.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.sync.task.engine.adapter.mybatis.entity.SyncTaskDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 同步任务 Mapper。
 *
 * @author sync-task-engine
 * @date 2026-07-16
 */
@Mapper
public interface SyncTaskMapper extends BaseMapper<SyncTaskDO> {

    /**
     * 按状态拉取待处理任务，按状态权重排序。
     *
     * @param statuses 状态列表（INIT/FAIL/WAIT）
     * @param limit    拉取上限
     * @return 待处理任务列表
     */
    List<SyncTaskDO> selectPendingTasks(@Param("statuses") List<String> statuses,
                                         @Param("limit") int limit);

    /**
     * 递增重试次数。
     *
     * @param ids 任务 ID 列表
     * @return 更新行数
     */
    int incrementRetryTimes(@Param("ids") List<Long> ids);

    /**
     * 将 SUCCESS 任务移入归档表并删除原记录。
     *
     * @param beforeTime 归档此时间之前的记录
     * @return 归档条数
     */
    int archiveSuccessTasks(@Param("beforeTime") java.time.LocalDateTime beforeTime);

    /**
     * 将 FAIL 且超过最大重试次数的任务移入归档表并删除原记录。
     *
     * @param beforeTime 归档此时间之前的记录
     * @return 归档条数
     */
    int archiveDeadTasks(@Param("beforeTime") java.time.LocalDateTime beforeTime);
}
