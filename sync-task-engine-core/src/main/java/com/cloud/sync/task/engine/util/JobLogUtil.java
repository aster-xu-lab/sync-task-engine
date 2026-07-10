package com.cloud.sync.task.engine.util;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 引擎日志工具类。
 * <p>提供 SLF4J 日志输出。调度平台日志通过 {@link com.cloud.sync.task.engine.executor.SchedulerAdapter} 输出。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public final class JobLogUtil {

    private static final Logger log = LoggerFactory.getLogger(JobLogUtil.class);

    private JobLogUtil() {}

    /**
     * 输出 INFO 日志（仅 SLF4J）。
     */
    public static void logInfo(String pattern, Object... values) {
        logInfo(log, pattern, values);
    }

    /**
     * 输出 INFO 日志（指定 Logger）。
     */
    public static void logInfo(Logger logger, String pattern, Object... values) {
        if (logger != null && StrUtil.isNotBlank(pattern)) {
            logger.info(pattern, values);
        }
    }

    /**
     * 输出 ERROR 日志（仅 SLF4J）。
     */
    public static void logError(String pattern, Exception e, Object... values) {
        logError(log, pattern, e, values);
    }

    /**
     * 输出 ERROR 日志（指定 Logger）。
     */
    public static void logError(Logger logger, String pattern, Exception e, Object... values) {
        if (logger != null && StrUtil.isNotBlank(pattern)) {
            Object[] args = new Object[values.length + 1];
            System.arraycopy(values, 0, args, 0, values.length);
            args[values.length] = e;
            logger.error(pattern, args);
        }
    }
}
