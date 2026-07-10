package com.cloud.sync.task.engine.adapter.xxljob;

import com.cloud.sync.task.engine.executor.SyncTaskEngine;
import com.cloud.sync.task.engine.registry.SyncTaskHandlerRegistry;
import com.cloud.sync.task.engine.spi.SyncTaskParamValidator;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * XXL-JOB 调度入口桥接器。
 *
 * <p>将 XXL-JOB 的 @XxlJob 注解与 SyncTaskEngine 桥接。
 * 接入方在 xxl-job 控制台配置 JobHandler="syncTaskJob" 即可。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public class XxlJobSyncTaskLauncher<T, C> {

    private static final Logger log = LoggerFactory.getLogger(XxlJobSyncTaskLauncher.class);

    private final SyncTaskEngine<T, C> engine;
    private final Function<String, SyncTaskEngine.SyncTaskParamSupplier> paramParser;

    /**
     * @param registry    Handler 注册表（必需）
     * @param validator   参数校验器（可选，null 表示跳过）
     * @param paramParser 参数解析器（JSON → SyncTaskParam 实现类）
     */
    public XxlJobSyncTaskLauncher(SyncTaskHandlerRegistry<T, C> registry,
                                   SyncTaskParamValidator validator,
                                   Function<String, SyncTaskEngine.SyncTaskParamSupplier> paramParser) {
        SyncTaskEngine.Builder<T, C> builder = new SyncTaskEngine.Builder<T, C>()
                .registry(registry)
                .schedulerAdapter(new XxlJobSchedulerAdapter());
        if (validator != null) {
            builder.validators(java.util.Collections.singletonList(validator));
        }
        this.engine = builder.build();
        this.paramParser = paramParser;
    }

    /**
     * XXL-JOB 调度入口。JobHandler="syncTaskJob"。
     */
    @XxlJob("syncTaskJob")
    public void syncTaskJob() throws InterruptedException {
        String rawParam = XxlJobHelper.getJobParam();
        SyncTaskEngine.SyncTaskParamSupplier supplier = paramParser.apply(rawParam);
        engine.execute(supplier);
    }
}
