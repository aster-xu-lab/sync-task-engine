package com.cloud.sync.task.engine.adapter.mybatis;

import com.cloud.sync.task.engine.spi.TaskArchiveService;
import com.cloud.sync.task.engine.spi.TaskStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * MyBatis 适配器自动装配。
 * <p>当 classpath 中存在 MyBatis-Plus 时自动生效。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-15
 */
@AutoConfiguration
@ConditionalOnClass(com.baomidou.mybatisplus.core.mapper.BaseMapper.class)
public class MybatisAutoConfiguration {

    /**
     * TaskStore 默认实现（空壳，接入方继承覆写）。
     * <p>仅当接入方未提供自定义 TaskStore 时生效。</p>
     */
    @Bean
    @ConditionalOnMissingBean(TaskStore.class)
    @SuppressWarnings("rawtypes")
    public MybatisTaskStore<Object> mybatisTaskStore() {
        return new MybatisTaskStore<>();
    }

    /**
     * TaskArchiveService 默认实现（空壳，接入方继承覆写）。
     * <p>仅当接入方未提供自定义 TaskArchiveService 时生效。</p>
     */
    @Bean
    @ConditionalOnMissingBean(TaskArchiveService.class)
    @SuppressWarnings("rawtypes")
    public MybatisTaskArchiveService<Object> mybatisTaskArchiveService() {
        return new MybatisTaskArchiveService<>();
    }
}
