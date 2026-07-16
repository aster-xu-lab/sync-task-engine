package com.cloud.sync.task.engine.adapter.xxljob;

import com.cloud.sync.task.engine.executor.SchedulerAdapter;
import com.xxl.job.core.context.XxlJobHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XXL-JOB 调度框架适配器。
 *
 * <p>通过 XxlJobHelper 获取参数和输出日志。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public class XxlJobSchedulerAdapter implements SchedulerAdapter {

    private static final Logger log = LoggerFactory.getLogger(XxlJobSchedulerAdapter.class);

    @Override
    public String resolveJobParam() {
        return XxlJobHelper.getJobParam();
    }

    @Override
    public void logInfo(String pattern, Object... args) {
        log.info(pattern, args);
        XxlJobHelper.log(pattern, args);
    }

    @Override
    public void logError(String pattern, Object... args) {
        log.error(pattern, args);
        XxlJobHelper.log(pattern, args);
    }
}
