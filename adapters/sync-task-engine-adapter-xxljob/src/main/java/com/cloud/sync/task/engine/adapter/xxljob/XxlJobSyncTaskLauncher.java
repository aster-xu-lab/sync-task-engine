package com.cloud.sync.task.engine.adapter.xxljob;

import com.cloud.sync.task.engine.SyncTaskEngineConstants;
import com.cloud.sync.task.engine.executor.SyncTaskEngine;
import com.cloud.sync.task.engine.spi.SyncTaskParamParser;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XXL-JOB 调度入口桥接器。
 *
 * <p>将 XXL-JOB 的 {@code @XxlJob} 注解与 {@link SyncTaskEngine} 桥接。
 * 接入方在 xxl-job 控制台配置 JobHandler="syncTaskJob" 即可。</p>
 *
 * <p>由 {@link XxlJobAutoConfiguration} 自动装配为 Spring Bean，接入方无需手动创建。</p>
 *
 * @param <T> 任务业务对象类型
 * @param <C> 任务上下文类型
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public class XxlJobSyncTaskLauncher<T, C> {

    private static final Logger log = LoggerFactory.getLogger(XxlJobSyncTaskLauncher.class);

    private final SyncTaskEngine<T, C> engine;
    private final SyncTaskParamParser paramParser;

    /**
     * @param engine      已装配好的引擎实例
     * @param paramParser 参数解析器
     */
    public XxlJobSyncTaskLauncher(SyncTaskEngine<T, C> engine, SyncTaskParamParser paramParser) {
        this.engine = engine;
        this.paramParser = paramParser;
    }

    /**
     * XXL-JOB 调度入口。JobHandler="syncTaskJob"。
     */
    @XxlJob(SyncTaskEngineConstants.XXL_JOB_HANDLER_NAME)
    public void syncTaskJob() throws InterruptedException {
        String rawParam = XxlJobHelper.getJobParam();
        SyncTaskEngine.SyncTaskParamSupplier supplier = paramParser.parse(rawParam);
        engine.execute(supplier);
    }
}
