package com.cloud.sync.task.engine.registry;

import com.cloud.sync.task.engine.spi.SyncTaskHandler;
import com.cloud.sync.task.engine.spi.SyncTaskParam;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同步任务处理器注册表。
 * <p>线程安全的 Handler 注册与查找。接入方负责在启动时调用 {@link #register} 将所有 Handler 注册进来，
 * 通常通过 Spring Bean 扫描完成。</p>
 *
 * @param <T> 任务业务对象类型
 * @param <C> 任务上下文类型
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public class SyncTaskHandlerRegistry<T, C> {

    private final Map<String, SyncTaskHandler<T, C>> handlerMap = new ConcurrentHashMap<>();

    /**
     * 注册一个 Handler。
     *
     * @param handler Handler 实例
     * @throws IllegalStateException 如果 key 重复
     */
    public void register(SyncTaskHandler<T, C> handler) {
        String key = handler.getHandlerKey();
        SyncTaskHandler<T, C> existing = handlerMap.putIfAbsent(key, handler);
        if (existing != null && existing != handler) {
            throw new IllegalStateException(
                String.format("任务处理器 key 重复: [%s], 已存在 [%s], 新注册 [%s]",
                    key, existing.getClass().getName(), handler.getClass().getName()));
        }
    }

    /**
     * 根据参数获取对应的 Handler。
     *
     * @param param 任务参数
     * @return Handler，未找到返回 null
     */
    public SyncTaskHandler<T, C> getHandler(SyncTaskParam param) {
        // 引擎不预设 key 格式，让 Handler 自己决定
        // 但提供便捷方法：按 Handler 自己的 key 匹配
        for (SyncTaskHandler<T, C> handler : handlerMap.values()) {
            if (handler.getHandlerKey().equals(buildExpectedKey(param, handler))) {
                return handler;
            }
        }
        return null;
    }

    /**
     * 获取只读 Handler Map（用于调试和监控）。
     */
    public Map<String, SyncTaskHandler<T, C>> getAllHandlers() {
        return Collections.unmodifiableMap(handlerMap);
    }

    /**
     * 通过 Handler 的 getHandlerKey() 直接匹配，简单场景直接用。
     * 复杂场景（如带 handlerSuffix）由接入方自行构建 key 并调用 {@link #getByKey}。
     */
    public SyncTaskHandler<T, C> getByKey(String key) {
        return handlerMap.get(key);
    }

    /**
     * 注册表大小。
     */
    public int size() {
        return handlerMap.size();
    }

    /**
     * 构建给定参数和 Handler 的期望 key。
     * 默认行为：直接返回 handler.getHandlerKey()。
     * 子类或接入方可以覆盖来自定义 key 匹配逻辑。
     */
    protected String buildExpectedKey(SyncTaskParam param, SyncTaskHandler<T, C> handler) {
        return handler.getHandlerKey();
    }
}
