package com.cloud.sync.task.engine.spi;

import com.cloud.sync.task.engine.executor.SyncTaskEngine;

/**
 * 同步任务参数解析器 SPI。
 * <p>将调度框架传入的原始 JSON 字符串解析为 {@link SyncTaskParam} 实例。
 * 接入方实现此接口并注册为 Spring Bean，引擎自动发现并用于参数解析。</p>
 *
 * <p>简单场景下无需实现此接口——在 application.yml 中配置
 * {@code sync-task.engine.param-class} 即可，框架会通过反射 + Jackson 自动解析。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-15
 */
@FunctionalInterface
public interface SyncTaskParamParser {

    /**
     * 将原始 JSON 字符串解析为参数提供者。
     *
     * @param rawJson 原始 JSON 字符串（来自调度平台）
     * @return 参数提供者，每次调用 parse() 返回一个新的 SyncTaskParam 实例
     */
    SyncTaskEngine.SyncTaskParamSupplier parse(String rawJson);
}
