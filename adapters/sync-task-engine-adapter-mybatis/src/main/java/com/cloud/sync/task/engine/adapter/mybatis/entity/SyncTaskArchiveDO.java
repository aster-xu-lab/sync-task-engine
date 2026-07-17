package com.cloud.sync.task.engine.adapter.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 同步任务归档表实体（标准表结构）。
 * <p>与 {@link SyncTaskDO} 结构一致，用于存放已完成/死信任务的历史数据。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-16
 */
@TableName("sync_task_archive")
public class SyncTaskArchiveDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键（保留原任务ID） */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 业务类型 */
    private Integer bizType;

    /** 同步任务类型（可选） */
    private Integer syncTaskType;

    /** 目标系统 */
    private Integer syncSystem;

    /** 基准业务编号 */
    private String referenceNo;

    /** 来源编号 */
    private String sourceNo;

    /** 归档前最终状态：SUCCESS / FAIL */
    private String taskStatus;

    /** 总重试次数 */
    private Integer retryTimes;

    /** 错误信息 */
    private String errorMessage;

    /** 同步成功时间 */
    private LocalDateTime syncSuccessTime;

    /** 原任务创建时间 */
    private LocalDateTime originalCreateTime;

    /** 原任务更新时间 */
    private LocalDateTime originalUpdateTime;

    /** 归档时间 */
    private LocalDateTime archiveTime;

    // ==================== getter/setter ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Integer getSyncTaskType() {
        return syncTaskType;
    }

    public void setSyncTaskType(Integer syncTaskType) {
        this.syncTaskType = syncTaskType;
    }

    public Integer getSyncSystem() {
        return syncSystem;
    }

    public void setSyncSystem(Integer syncSystem) {
        this.syncSystem = syncSystem;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getSyncSuccessTime() {
        return syncSuccessTime;
    }

    public void setSyncSuccessTime(LocalDateTime syncSuccessTime) {
        this.syncSuccessTime = syncSuccessTime;
    }

    public LocalDateTime getOriginalCreateTime() {
        return originalCreateTime;
    }

    public void setOriginalCreateTime(LocalDateTime originalCreateTime) {
        this.originalCreateTime = originalCreateTime;
    }

    public LocalDateTime getOriginalUpdateTime() {
        return originalUpdateTime;
    }

    public void setOriginalUpdateTime(LocalDateTime originalUpdateTime) {
        this.originalUpdateTime = originalUpdateTime;
    }

    public LocalDateTime getArchiveTime() {
        return archiveTime;
    }

    public void setArchiveTime(LocalDateTime archiveTime) {
        this.archiveTime = archiveTime;
    }

    @Override
    public String toString() {
        return "SyncTaskArchiveDO{id=" + id + ", status=" + taskStatus + "}";
    }
}
