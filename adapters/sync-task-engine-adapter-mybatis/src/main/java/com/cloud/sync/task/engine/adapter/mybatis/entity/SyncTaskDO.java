package com.cloud.sync.task.engine.adapter.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 同步任务表实体（标准表结构）。
 * <p>引擎默认的表结构定义，接入方可在此基础上添加业务字段。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-16
 */
@TableName("sync_task")
public class SyncTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 单据类型（销售/采购/售后/开票...） */
    private Integer orderType;

    /** 同步任务类型（可选） */
    private Integer syncTaskType;

    /** 目标系统 */
    private Integer syncSystem;

    /** 来源单号 */
    private String sourceOrderCode;

    /** 任务状态：INIT / WAIT / PROCESSING / SUCCESS / FAIL */
    private String taskStatus;

    /** 已重试次数 */
    private Integer retryTimes;

    /** 错误信息 */
    private String errorMessage;

    /** 同步成功时间 */
    private LocalDateTime syncSuccessTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // ==================== getter/setter ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
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

    public String getSourceOrderCode() {
        return sourceOrderCode;
    }

    public void setSourceOrderCode(String sourceOrderCode) {
        this.sourceOrderCode = sourceOrderCode;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "SyncTaskDO{id=" + id + ", status=" + taskStatus + ", sourceOrder=" + sourceOrderCode + "}";
    }
}
