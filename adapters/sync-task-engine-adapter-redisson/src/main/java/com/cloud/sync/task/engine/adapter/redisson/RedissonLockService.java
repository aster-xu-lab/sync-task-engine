package com.cloud.sync.task.engine.adapter.redisson;

import com.cloud.sync.task.engine.spi.LockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Redisson 分布式锁适配器。
 *
 * @author sync-task-engine
 * @date 2026-07-15
 */
public class RedissonLockService implements LockService {

    private static final Logger log = LoggerFactory.getLogger(RedissonLockService.class);

    private final RedissonClient redissonClient;

    public RedissonLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean tryLock(String key, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[RedissonLock] 获取锁被中断, key={}", key, e);
            return false;
        }
    }

    @Override
    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
