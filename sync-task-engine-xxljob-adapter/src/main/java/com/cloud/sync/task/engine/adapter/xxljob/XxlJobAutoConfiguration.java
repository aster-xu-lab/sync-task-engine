package com.cloud.sync.task.engine.adapter.xxljob;

import com.cloud.sync.task.engine.executor.SchedulerAdapter;
import com.cloud.sync.task.engine.executor.SyncTaskEngine;
import com.cloud.sync.task.engine.registry.SyncTaskHandlerRegistry;
import com.cloud.sync.task.engine.spi.*;
import com.xxl.job.core.context.XxlJobHelper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.List;

/**
 * XXL-JOB 适配器自动装配。
 *
 * <p>当 classpath 中存在 xxl-job-core 且接入方提供了
 * {@link SyncTaskHandlerRegistry} 和 {@link SyncTaskParamParser} 时自动生效。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-15
 */
@AutoConfiguration
@ConditionalOnClass(XxlJobHelper.class)
@ConditionalOnBean({SyncTaskHandlerRegistry.class, SyncTaskParamParser.class})
public class XxlJobAutoConfiguration {

    /**
     * XXL-JOB 调度适配器 Bean。
     * <p>如果接入方提供了自定义 SchedulerAdapter，此默认实现不生效。</p>
     */
    @Bean
    @ConditionalOnMissingBean(SchedulerAdapter.class)
    public XxlJobSchedulerAdapter xxlJobSchedulerAdapter() {
        return new XxlJobSchedulerAdapter();
    }

    /**
     * XXL-JOB 桥接启动器 Bean。
     * <p>自动装配 SyncTaskEngine 并桥接到 @XxlJob("syncTaskJob")。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public XxlJobSyncTaskLauncher<Object, Object> xxlJobSyncTaskLauncher(
            SyncTaskHandlerRegistry<Object, Object> registry,
            SchedulerAdapter schedulerAdapter,
            SyncTaskParamParser paramParser,
            ObjectProvider<List<SyncTaskParamValidator>> validatorsProvider,
            ObjectProvider<List<TaskFetchFilter<Object>>> fetchFiltersProvider,
            ObjectProvider<List<TaskLifecycleListener<Object>>> listenersProvider,
            ObjectProvider<LockService> lockServiceProvider,
            ObjectProvider<NotifyChannel> notifyChannelProvider) {

        SyncTaskEngine.Builder<Object, Object> builder = new SyncTaskEngine.Builder<Object, Object>()
                .registry(registry)
                .schedulerAdapter(schedulerAdapter);

        validatorsProvider.ifAvailable(builder::validators);
        fetchFiltersProvider.ifAvailable(builder::fetchFilters);
        listenersProvider.ifAvailable(builder::listeners);
        lockServiceProvider.ifAvailable(builder::lockService);
        notifyChannelProvider.ifAvailable(builder::notifyChannel);

        SyncTaskEngine<Object, Object> engine = builder.build();
        return new XxlJobSyncTaskLauncher<>(engine, paramParser);
    }
}
