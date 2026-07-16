package com.cloud.sync.task.engine.adapter.mybatis;

import com.cloud.sync.task.engine.adapter.mybatis.mapper.SyncTaskMapper;
import com.cloud.sync.task.engine.spi.TaskArchiveService;
import com.cloud.sync.task.engine.spi.TaskStore;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * MyBatis 适配器自动装配。
 * <p>当 classpath 中存在 MyBatis-Plus 时自动生效。
 * 自动扫描本模块 Mapper 并注册 TaskStore / TaskArchiveService 默认实现。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-16
 */
@AutoConfiguration
@ConditionalOnClass(com.baomidou.mybatisplus.core.mapper.BaseMapper.class)
@MapperScan("com.cloud.sync.task.engine.adapter.mybatis.mapper")
public class MybatisAutoConfiguration {

    /**
     * TaskStore 默认实现。
     */
    @Bean
    @ConditionalOnMissingBean(TaskStore.class)
    public MybatisTaskStore mybatisTaskStore(SyncTaskMapper syncTaskMapper) {
        return new MybatisTaskStore(syncTaskMapper);
    }

    /**
     * TaskArchiveService 默认实现。
     */
    @Bean
    @ConditionalOnMissingBean(TaskArchiveService.class)
    public MybatisTaskArchiveService mybatisTaskArchiveService(SyncTaskMapper syncTaskMapper) {
        return new MybatisTaskArchiveService(syncTaskMapper);
    }
}
