-- ============================================================
-- SyncTask Engine 标准表结构（MySQL）
-- ============================================================

-- ----------------------------
-- 同步任务表（任务队列 + 状态机）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sync_task` (
    `id`                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_type`        INT           NOT NULL                COMMENT '单据类型',
    `sync_task_type`    INT           DEFAULT NULL            COMMENT '同步任务类型（可选）',
    `sync_system`       INT           NOT NULL                COMMENT '目标系统',
    `source_order_code` VARCHAR(64)   DEFAULT NULL            COMMENT '来源单号',
    `task_status`       VARCHAR(16)   NOT NULL DEFAULT 'INIT' COMMENT '任务状态：INIT/WAIT/PROCESSING/SUCCESS/FAIL',
    `retry_times`       INT           DEFAULT 0               COMMENT '已重试次数',
    `error_message`     TEXT          DEFAULT NULL            COMMENT '错误信息',
    `sync_success_time` DATETIME      DEFAULT NULL            COMMENT '同步成功时间',
    `create_time`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`task_status`),
    INDEX `idx_order_system` (`order_type`, `sync_system`),
    INDEX `idx_source_order` (`source_order_code`),
    INDEX `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步任务表';

-- ----------------------------
-- 同步任务归档表（历史数据）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sync_task_archive` (
    `id`                  BIGINT        NOT NULL              COMMENT '主键（保留原任务ID）',
    `order_type`          INT           NOT NULL              COMMENT '单据类型',
    `sync_task_type`      INT           DEFAULT NULL          COMMENT '同步任务类型（可选）',
    `sync_system`         INT           NOT NULL              COMMENT '目标系统',
    `source_order_code`   VARCHAR(64)   DEFAULT NULL          COMMENT '来源单号',
    `task_status`         VARCHAR(16)   NOT NULL              COMMENT '归档前最终状态：SUCCESS/FAIL',
    `retry_times`         INT           DEFAULT 0             COMMENT '总重试次数',
    `error_message`       TEXT          DEFAULT NULL          COMMENT '错误信息',
    `sync_success_time`   DATETIME      DEFAULT NULL          COMMENT '同步成功时间',
    `original_create_time` DATETIME     NOT NULL              COMMENT '原任务创建时间',
    `original_update_time` DATETIME     NOT NULL              COMMENT '原任务更新时间',
    `archive_time`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '归档时间',
    PRIMARY KEY (`id`),
    INDEX `idx_archive_time` (`archive_time`),
    INDEX `idx_order_system` (`order_type`, `sync_system`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步任务归档表';
