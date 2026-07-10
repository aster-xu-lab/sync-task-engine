package com.cloud.sync.task.engine.spi;

import java.util.function.Supplier;

/**
 * 分布式锁服务抽象（SPI）。
 *
 * <p>引擎通过此接口加锁防重，不绑定具体锁框架（Redisson/@XLock/ShedLock/DB 行锁）。
 * 接入方负责实现具体的加锁/解锁逻辑。</p>
 *
 * <p>这是 SyncTaskJob 中直接依赖 @XLock 注解的替代方案——
 * 原来通过 AOP 注解方式加锁，现在改为编程式接口调用，
 * 避免注解框架的兼容性问题。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public interface LockService {

    /**
     * 尝试获取锁。
     *
     * @param key       锁的唯一标识
     * @param waitTime  最大等待时间（秒）
     * @param leaseTime 锁持有时间（秒）
     * @return 是否获取成功
     */
    boolean tryLock(String key, int waitTime, int leaseTime);

    /**
     * 释放锁。
     */
    void unlock(String key);

    /**
     * 在锁保护下执行逻辑（推荐使用，自动释放锁）。
     *
     * @param key          锁的唯一标识
     * @param waitTime     最大等待时间（秒）
     * @param leaseTime    锁持有时间（秒）
     * @param errorMessage 获取锁失败时的错误消息
     * @param action       要执行的业务逻辑
     * @return 业务逻辑的返回值
     * @throws LockAcquireException 获取锁失败时抛出
     */
    default <T> T executeWithLock(String key, int waitTime, int leaseTime,
                                   String errorMessage,
                                   Supplier<T> action) {
        if (!tryLock(key, waitTime, leaseTime)) {
            throw new LockAcquireException(errorMessage);
        }
        try {
            return action.get();
        } finally {
            unlock(key);
        }
    }

    /**
     * 获取锁失败异常。
     */
    class LockAcquireException extends RuntimeException {
        public LockAcquireException(String message) {
            super(message);
        }
    }
}
