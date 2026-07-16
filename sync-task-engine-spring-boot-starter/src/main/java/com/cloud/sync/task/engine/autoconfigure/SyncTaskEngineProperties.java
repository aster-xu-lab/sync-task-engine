package com.cloud.sync.task.engine.autoconfigure;

import com.cloud.sync.task.engine.SyncTaskEngineConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SyncTask Engine 配置属性。
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
@ConfigurationProperties(prefix = SyncTaskEngineConstants.CONFIG_PREFIX)
public class SyncTaskEngineProperties {

    /** 是否启用，默认 true */
    private boolean enabled = true;

    /**
     * SyncTaskParam 实现类的全限定名。
     * <p>配置此项后，框架会通过反射 + Jackson 自动解析调度参数 JSON。
     * 复杂场景（字段重命名、默认值填充）建议实现 {@code SyncTaskParamParser} Bean 替代。</p>
     */
    private String paramClass;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getParamClass() {
        return paramClass;
    }

    public void setParamClass(String paramClass) {
        this.paramClass = paramClass;
    }
}
