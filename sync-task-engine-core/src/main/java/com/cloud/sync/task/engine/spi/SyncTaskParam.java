package com.cloud.sync.task.engine.spi;

import com.cloud.sync.task.engine.SyncTaskEngineConstants;

import java.util.List;

/**
 * 任务调度参数接口。
 * <p>接入方（cloud-order/cloud-wms）的 SyncTaskJobParamConfig DTO 实现此接口，
 * 引擎通过此接口获取路由所需的必要信息，不关心各项目的特有字段。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public interface SyncTaskParam {

    /**
     * 获取业务类型（不限定"单据"，适用于任何需要同步的业务场景）。
     */
    Integer getBizType();

    /**
     * 获取同步任务类型（与 bizType 为 1:N 关系，如创建/修改/发货/取消）。
     */
    Integer getSyncTaskType();

    /**
     * 获取目标系统。
     */
    Integer getSyncSystem();

    /**
     * 获取处理器后缀（用于同一维度下的二次分发）。
     */
    default String getHandlerSuffix() {
        return null;
    }

    /**
     * 获取一批处理任务最大数，默认 100。
     */
    default Integer getSize() {
        return SyncTaskEngineConstants.DEFAULT_FETCH_SIZE;
    }

    /**
     * 获取最大重试次数，默认 5。
     */
    default Integer getLimitRetryTimes() {
        return SyncTaskEngineConstants.DEFAULT_MAX_RETRY_TIMES;
    }

    /**
     * 获取告警阈值，默认 1。
     */
    default Integer getWarningErrorThreshold() {
        return 1;
    }

    /**
     * 获取状态权重数组 [INIT, FAIL, WAIT]，默认 4:1:1。
     */
    default int[] getStatusWeights() {
        return SyncTaskEngineConstants.DEFAULT_STATUS_WEIGHTS;
    }

    /**
     * 获取错误消息规则：0-SKIP, 1-ONE_THRESHOLD, 2-BATCH_THRESHOLD, 3-MIN_THRESHOLD。
     */
    default int getErrorMessageRule() {
        return SyncTaskEngineConstants.DEFAULT_ERROR_MSG_RULE;
    }

    /**
     * 获取错误 Excel 文件名。
     */
    default String getErrorExcelName() {
        return null;
    }

    /**
     * 获取指定编号列表（手动执行场景）。
     */
    default List<String> getSourceNos() {
        return null;
    }

    /**
     * 获取批处理大小，默认 20（V2 处理器生效）。
     */
    default Integer getBatchSize() {
        return SyncTaskEngineConstants.DEFAULT_BATCH_SIZE;
    }

    /**
     * 获取自定义线程池名称。
     */
    default String getCustomerThreadPool() {
        return null;
    }

    /**
     * 获取扩展属性（各项目存放特有字段）。
     */
    default java.util.Map<String, Object> getExtensions() {
        return null;
    }
}
