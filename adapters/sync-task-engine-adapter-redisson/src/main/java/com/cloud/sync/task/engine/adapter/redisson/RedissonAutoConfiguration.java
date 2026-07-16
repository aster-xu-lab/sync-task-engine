package com.cloud.sync.task.engine.adapter.redisson;

import com.cloud.sync.task.engine.spi.LockService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Redisson 适配器自动装配。
 * <p>当 classpath 中存在 Redisson 且容器中有 RedissonClient 时自动生效。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-15
 */
@AutoConfiguration
@ConditionalOnClass(RedissonClient.class)
public class RedissonAutoConfiguration {

    /**
     * Redisson 分布式锁适配器。
     */
    @Bean
    @ConditionalOnMissingBean(LockService.class)
    @ConditionalOnBean(RedissonClient.class)
    public RedissonLockService redissonLockService(RedissonClient redissonClient) {
        return new RedissonLockService(redissonClient);
    }
}
