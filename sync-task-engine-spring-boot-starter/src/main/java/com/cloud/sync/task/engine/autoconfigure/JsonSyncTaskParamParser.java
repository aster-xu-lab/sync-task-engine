package com.cloud.sync.task.engine.autoconfigure;

import com.cloud.sync.task.engine.executor.SyncTaskEngine;
import com.cloud.sync.task.engine.spi.SyncTaskParam;
import com.cloud.sync.task.engine.spi.SyncTaskParamParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于 Jackson 的 JSON 参数解析器默认实现。
 * <p>通过反射将原始 JSON 字符串反序列化为 {@link SyncTaskParam} 子类实例。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-15
 */
public class JsonSyncTaskParamParser implements SyncTaskParamParser {

    private static final Logger log = LoggerFactory.getLogger(JsonSyncTaskParamParser.class);

    private final Class<? extends SyncTaskParam> paramClass;
    private final ObjectMapper objectMapper;

    public JsonSyncTaskParamParser(Class<? extends SyncTaskParam> paramClass) {
        this.paramClass = paramClass;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public SyncTaskEngine.SyncTaskParamSupplier parse(String rawJson) {
        return json -> {
            try {
                return objectMapper.readValue(json, paramClass);
            } catch (Exception e) {
                log.error("参数解析失败, rawJson={}, paramClass={}", json, paramClass.getName(), e);
                throw new RuntimeException("Failed to parse SyncTaskParam: " + json, e);
            }
        };
    }
}
