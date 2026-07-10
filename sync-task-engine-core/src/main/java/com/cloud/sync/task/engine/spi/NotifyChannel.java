package com.cloud.sync.task.engine.spi;

/**
 * 通知渠道抽象（SPI）。
 *
 * <p>引擎通过此接口发送告警通知，不绑定具体渠道（企微/飞书/钉钉/邮件）。
 * 接入方负责实现具体渠道的消息推送逻辑。</p>
 *
 * <p>这是 SyncTaskJob 中直接依赖 QWRobotUtil 和 ISysDictDataService 的替代方案——
 * 原来 Handler 基类硬编码了企微机器人的 Markdown + Excel 发送方式，
 * 现在由引擎通过此接口统一发送，渠道可自由替换。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public interface NotifyChannel {

    /**
     * 发送文本/Markdown 消息。
     *
     * @param content 消息内容
     * @param level   告警级别
     * @return 是否发送成功
     */
    boolean sendMessage(String content, NotifyLevel level);

    /**
     * 发送文件（如 Excel 报表）。
     *
     * @param fileBytes 文件字节数组
     * @param fileName  文件名
     * @param level     告警级别
     * @return 是否发送成功
     */
    boolean sendFile(byte[] fileBytes, String fileName, NotifyLevel level);

    /**
     * 是否启用。返回 false 时引擎跳过所有通知。
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * 告警级别。
     */
    enum NotifyLevel {
        INFO,
        WARN,
        ERROR
    }
}
