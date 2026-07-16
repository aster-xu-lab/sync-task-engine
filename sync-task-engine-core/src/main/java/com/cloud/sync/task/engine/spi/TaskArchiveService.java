package com.cloud.sync.task.engine.spi;

import java.util.Date;

/**
 * 任务归档服务 SPI。
 *
 * <p>引擎通过此接口提供归档能力扩展点。当 sync_task 表数据量过大时，
 * 接入方实现此接口将历史任务数据归档到历史表或直接删除。</p>
 *
 * <p>设计原则：
 * <ul>
 *   <li>引擎只定义 SPI，不内置定时任务——由接入方用 xxl-job cron 调度</li>
 *   <li>{@link #advice()} 仅给出建议，不执行实际操作——引擎不替业务做决定</li>
 *   <li>不强制——接入方不实现此 SPI 就跳过，不影响引擎正常运行</li>
 * </ul>
 *
 * <p>典型使用方式：
 * <pre>
 * // 1. 实现 SPI Bean
 * &#64;Component
 * public class MyTaskArchiveService implements TaskArchiveService&lt;SyncTaskBO&gt; {
 *     &#64;Override
 *     public int archiveSuccessTasks(Date beforeTime) {
 *         return mapper.moveToArchive(beforeTime, "SUCCESS");
 *     }
 *     &#64;Override
 *     public int archiveDeadTasks(Date beforeTime) {
 *         return mapper.moveToArchive(beforeTime, "DEAD");
 *     }
 * }
 *
 * // 2. 注册 xxl-job 定时触发
 * &#64;XxlJob("archiveTaskJob")
 * public void archiveTaskJob() {
 *     int count = archiveService.archiveSuccessTasks(DateUtils.addDays(new Date(), -30));
 *     XxlJobHelper.log("归档 {} 条", count);
 * }
 * </pre>
 *
 * @param <T> 任务业务对象类型
 *
 * @author sync-task-engine
 * @date 2026-07-16
 */
public interface TaskArchiveService<T> {

    /**
     * 归档已成功的历史任务。
     *
     * @param beforeTime 归档此时间之前的记录（含）
     * @return 实际归档条数
     */
    int archiveSuccessTasks(Date beforeTime);

    /**
     * 归档已失败且不再重试的死信任务（超过最大重试次数）。
     *
     * @param beforeTime 归档此时间之前的记录（含）
     * @return 实际归档条数
     */
    int archiveDeadTasks(Date beforeTime);

    /**
     * 引擎给出归档建议（仅建议，不执行实际操作）。
     * <p>引擎在批次完成后可调用此方法检查业务方是否有实现，
     * 接入方也可定期调用此方法判断是否需要触发归档。</p>
     *
     * @return 归档建议
     */
    default ArchiveAdvice advice() {
        return new ArchiveAdvice();
    }

    /**
     * 归档建议数据。
     */
    class ArchiveAdvice {
        /** 建议归档的成功任务数（超过阈值时建议） */
        private long successTaskCount;
        /** 建议归档的死信任务数（超过阈值时建议） */
        private long deadTaskCount;

        public long getSuccessTaskCount() {
            return successTaskCount;
        }

        public void setSuccessTaskCount(long successTaskCount) {
            this.successTaskCount = successTaskCount;
        }

        public long getDeadTaskCount() {
            return deadTaskCount;
        }

        public void setDeadTaskCount(long deadTaskCount) {
            this.deadTaskCount = deadTaskCount;
        }

        public boolean shouldArchiveSuccess() {
            return successTaskCount > 0;
        }

        public boolean shouldArchiveDead() {
            return deadTaskCount > 0;
        }
    }
}
