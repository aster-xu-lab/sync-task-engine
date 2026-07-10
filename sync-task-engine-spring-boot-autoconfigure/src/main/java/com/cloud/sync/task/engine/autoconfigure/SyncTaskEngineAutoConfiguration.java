package com.cloud.sync.task.engine.autoconfigure;

import com.cloud.sync.task.engine.registry.SyncTaskHandlerRegistry;
import com.cloud.sync.task.engine.spi.SyncTaskHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * SyncTask Engine 自动装配。
 *
 * <p>接入方只需引入此模块，实现 SyncTaskHandler 接口并注册为 Spring Bean，
 * 引擎会自动发现并注册 Handler。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "sync-task.engine", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SyncTaskEngineProperties.class)
public class SyncTaskEngineAutoConfiguration {

    /**
     * 创建 Handler 注册表并自动注册所有 Handler Bean。
     */
    @Bean
    @ConditionalOnMissingBean
    public SyncTaskHandlerRegistry<Object, Object> syncTaskHandlerRegistry(
            ObjectProvider<List<SyncTaskHandler<Object, Object>>> handlersProvider) {

        SyncTaskHandlerRegistry<Object, Object> registry = new SyncTaskHandlerRegistry<>();

        handlersProvider.ifAvailable(handlers -> {
            for (SyncTaskHandler<Object, Object> handler : handlers) {
                registry.register(handler);
            }
        });

        return registry;
    }
}
