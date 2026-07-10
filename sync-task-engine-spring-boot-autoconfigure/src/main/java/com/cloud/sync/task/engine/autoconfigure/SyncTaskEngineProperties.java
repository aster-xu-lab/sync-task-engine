package com.cloud.sync.task.engine.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SyncTask Engine 配置属性。
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
@ConfigurationProperties(prefix = "sync-task.engine")
public class SyncTaskEngineProperties {

    /** 是否启用，默认 true */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
