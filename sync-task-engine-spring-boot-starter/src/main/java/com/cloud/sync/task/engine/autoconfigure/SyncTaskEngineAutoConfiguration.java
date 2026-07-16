package com.cloud.sync.task.engine.autoconfigure;

import com.cloud.sync.task.engine.registry.SyncTaskHandlerRegistry;
import com.cloud.sync.task.engine.spi.SyncTaskHandler;
import com.cloud.sync.task.engine.spi.SyncTaskParam;
import com.cloud.sync.task.engine.spi.SyncTaskParamParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
 * <p>提供默认的 JSON 参数解析器：配置 {@code sync-task.engine.param-class}
 * 即可，无需手写 ParamParser。复杂场景实现 {@link SyncTaskParamParser} Bean 即可覆盖默认实现。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "sync-task.engine", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SyncTaskEngineProperties.class)
public class SyncTaskEngineAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SyncTaskEngineAutoConfiguration.class);

    /**
     * 创建 Handler 注册表并自动注册所有 Handler Bean。
     */
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings({"rawtypes", "unchecked"})
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

    /**
     * 默认的 JSON 参数解析器。
     * <p>当接入方配置了 {@code sync-task.engine.param-class} 且没有提供自定义
     * {@link SyncTaskParamParser} Bean 时生效。</p>
     */
    @Bean
    @ConditionalOnMissingBean(SyncTaskParamParser.class)
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnProperty(prefix = "sync-task.engine", name = "param-class")
    @SuppressWarnings("unchecked")
    public SyncTaskParamParser defaultSyncTaskParamParser(SyncTaskEngineProperties properties) {
        String className = properties.getParamClass();
        try {
            Class<?> clazz = Class.forName(className);
            if (!SyncTaskParam.class.isAssignableFrom(clazz)) {
                throw new IllegalStateException(
                        "sync-task.engine.param-class 必须实现 SyncTaskParam 接口: " + className);
            }
            log.info("启用默认 JSON 参数解析器: {}", className);
            return new JsonSyncTaskParamParser((Class<? extends SyncTaskParam>) clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("找不到 sync-task.engine.param-class 指定的类: " + className, e);
        }
    }
}
