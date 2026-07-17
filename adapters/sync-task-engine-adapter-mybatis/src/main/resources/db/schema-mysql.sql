-- ============================================================
-- SyncTask Engine 标准表结构（MySQL）
-- ============================================================

-- ----------------------------
-- 同步任务表（任务队列 + 状态机）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sync_task` (
    `id`                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `biz_type`          INT           NOT NULL                COMMENT '业务类型（不限定"单据"）',
    `sync_task_type`    INT           DEFAULT NULL            COMMENT '同步任务类型（可选）',
    `sync_system`       INT           NOT NULL                COMMENT '目标系统',
    `reference_no`      VARCHAR(64)   DEFAULT NULL            COMMENT '基准业务编号（产生同步任务的主体编号）',
    `source_no`         VARCHAR(64)   DEFAULT NULL            COMMENT '来源编号（上游系统传入的编号）',
    `task_status`       VARCHAR(16)   NOT NULL DEFAULT 'INIT' COMMENT '任务状态：INIT/WAIT/PROCESSING/SUCCESS/FAIL',
    `retry_times`       INT           DEFAULT 0               COMMENT '已重试次数',
    `error_message`     TEXT          DEFAULT NULL            COMMENT '错误信息',
    `sync_success_time` DATETIME      DEFAULT NULL            COMMENT '同步成功时间',
    `create_time`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`task_status`),
    INDEX `idx_biz_system` (`biz_type`, `sync_system`),
    INDEX `idx_reference_no` (`reference_no`),
    INDEX `idx_source_no` (`source_no`),
    INDEX `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步任务表';

-- ----------------------------
-- 同步任务归档表（历史数据）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sync_task_archive` (
    `id`                  BIGINT        NOT NULL              COMMENT '主键（保留原任务ID）',
    `biz_type`            INT           NOT NULL              COMMENT '业务类型',
    `sync_task_type`      INT           DEFAULT NULL          COMMENT '同步任务类型（可选）',
    `sync_system`         INT           NOT NULL              COMMENT '目标系统',
    `reference_no`        VARCHAR(64)   DEFAULT NULL          COMMENT '基准业务编号',
    `source_no`           VARCHAR(64)   DEFAULT NULL          COMMENT '来源编号',
    `task_status`         VARCHAR(16)   NOT NULL              COMMENT '归档前最终状态：SUCCESS/FAIL',
    `retry_times`         INT           DEFAULT 0             COMMENT '总重试次数',
    `error_message`       TEXT          DEFAULT NULL          COMMENT '错误信息',
    `sync_success_time`   DATETIME      DEFAULT NULL          COMMENT '同步成功时间',
    `original_create_time` DATETIME     NOT NULL              COMMENT '原任务创建时间',
    `original_update_time` DATETIME     NOT NULL              COMMENT '原任务更新时间',
    `archive_time`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '归档时间',
    PRIMARY KEY (`id`),
    INDEX `idx_archive_time` (`archive_time`),
    INDEX `idx_biz_system` (`biz_type`, `sync_system`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步任务归档表';
