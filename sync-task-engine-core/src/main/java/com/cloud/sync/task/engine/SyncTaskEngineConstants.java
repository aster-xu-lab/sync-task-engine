package com.cloud.sync.task.engine;

/**
 * SyncTask Engine 常量定义。
 *
 * @author sync-task-engine
 * @date 2026-07-16
 */
public final class SyncTaskEngineConstants {

    private SyncTaskEngineConstants() {
    }

    // ======================== 默认值 ========================

    /** 默认一次拉取任务最大数 */
    public static final int DEFAULT_FETCH_SIZE = 100;

    /** 默认批处理大小 */
    public static final int DEFAULT_BATCH_SIZE = 20;

    /** 默认最大重试次数 */
    public static final int DEFAULT_MAX_RETRY_TIMES = 5;

    /** 默认告警阈值 */
    public static final int DEFAULT_WARNING_THRESHOLD = 1;

    /** 默认状态权重 [INIT, FAIL, WAIT]，比例 4:1:1 */
    public static final int[] DEFAULT_STATUS_WEIGHTS = {4, 1, 1};

    /** 默认错误消息规则：1-ONE_THRESHOLD */
    public static final int DEFAULT_ERROR_MSG_RULE = 1;

    /** 默认 Validator 执行顺序（数字越小越先执行） */
    public static final int DEFAULT_VALIDATOR_ORDER = 0;

    // ======================== 配置 Key ========================

    /** 配置属性前缀 */
    public static final String CONFIG_PREFIX = "sync-task.engine";

    /** 参数类配置 key */
    public static final String CONFIG_KEY_ENABLED = "enabled";

    /** 参数类配置 key */
    public static final String CONFIG_KEY_PARAM_CLASS = "param-class";

    // ======================== XXL-JOB ========================

    /** XXL-JOB 调度入口 Handler 名称 */
    public static final String XXL_JOB_HANDLER_NAME = "syncTaskJob";
}
