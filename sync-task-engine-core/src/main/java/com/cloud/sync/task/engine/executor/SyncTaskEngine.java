package com.cloud.sync.task.engine.executor;

import com.cloud.sync.task.engine.registry.SyncTaskHandlerRegistry;
import com.cloud.sync.task.engine.spi.*;
import com.cloud.sync.task.engine.util.JobLogUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 同步任务编排引擎（完整版）。
 *
 * <p>完整的编排入口，支持七大 SPI 扩展点：
 * <ol>
 *   <li>{@link TaskStore} —— 任务存储（MyBatis/JPA/外部 API）</li>
 *   <li>{@link SchedulerAdapter} —— 调度框架适配（xxl-job/PowerJob/手动触发）</li>
 *   <li>{@link NotifyChannel} —— 告警通知渠道（企微/飞书/钉钉/邮件）</li>
 *   <li>{@link LockService} —— 分布式锁（Redisson/@XLock/DB 行锁）</li>
 *   <li>{@link SyncTaskParamValidator} —— 参数校验扩展</li>
 *   <li>{@link TaskFetchFilter} —— 任务拉取过滤</li>
 *   <li>{@link TaskLifecycleListener} —— 生命周期事件</li>
 *   <li>{@link TaskArchiveService} —— 历史任务归档</li>
 * </ol>
 * </p>
 *
 * <pre>
 * 典型使用方式（在 Spring Bean 中装配）：
 *
 *   &#64;Bean
 *   public SyncTaskEngine&lt;MyTaskBO, MyTaskContext&gt; syncTaskEngine(
 *           SyncTaskHandlerRegistry registry,
 *           SchedulerAdapter schedulerAdapter,
 *           List&lt;TaskLifecycleListener&lt;MyTaskBO&gt;&gt; listeners) {
 *       return new SyncTaskEngine.Builder&lt;MyTaskBO, MyTaskContext&gt;()
 *               .registry(registry)
 *               .schedulerAdapter(schedulerAdapter)
 *               .listeners(listeners)
 *               .build();
 *   }
 * </pre>
 *
 * @param <T> 任务业务对象类型
 * @param <C> 任务上下文类型
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
public class SyncTaskEngine<T, C> {

    private static final Logger log = LoggerFactory.getLogger(SyncTaskEngine.class);

    // ==================== 必要组件 ====================
    private final SyncTaskHandlerRegistry<T, C> registry;
    private final SchedulerAdapter schedulerAdapter;

    // ==================== 可选 SPI 扩展 ====================
    private final List<SyncTaskParamValidator> validators;
    private final List<TaskFetchFilter<T>> fetchFilters;
    private final List<TaskLifecycleListener<T>> listeners;
    private final LockService lockService;
    private final NotifyChannel notifyChannel;
    private final TaskArchiveService<T> archiveService;

    private SyncTaskEngine(Builder<T, C> builder) {
        this.registry = builder.registry;
        this.schedulerAdapter = builder.schedulerAdapter;
        this.validators = builder.validators;
        this.fetchFilters = builder.fetchFilters;
        this.listeners = builder.listeners;
        this.lockService = builder.lockService;
        this.notifyChannel = builder.notifyChannel;
        this.archiveService = builder.archiveService;
    }

    /**
     * 执行一次同步任务调度（完整流程）。
     *
     * @param paramSupplier 参数提供者（JSON → SyncTaskParam）
     * @throws InterruptedException 并发中断
     */
    public void execute(SyncTaskParamSupplier paramSupplier) throws InterruptedException {
        // 1. 获取调度参数
        String jobParam = schedulerAdapter.resolveJobParam();
        schedulerAdapter.logInfo("SyncTaskEngine 开始执行, 参数: {}", jobParam);

        if (StrUtil.isBlank(jobParam)) {
            schedulerAdapter.logInfo("参数为空，跳过执行");
            return;
        }

        // 2. 解析参数
        SyncTaskParam param = paramSupplier.parse(jobParam);

        // 3. 参数校验（按 Validator 顺序执行）
        if (CollUtil.isNotEmpty(validators)) {
            for (SyncTaskParamValidator validator : validators) {
                try {
                    validator.validate(param);
                } catch (SyncTaskParamValidator.ValidationException e) {
                    schedulerAdapter.logInfo("参数校验不通过: {}", e.getMessage());
                    return;
                }
            }
        }

        // 4. 路由获取处理器
        SyncTaskHandler<T, C> handler = registry.getHandler(param);
        if (ObjectUtil.isNull(handler)) {
            schedulerAdapter.logInfo("找不到对应的处理器, orderType={}, syncSystem={}",
                    param.getOrderType(), param.getSyncSystem());
            return;
        }
        schedulerAdapter.logInfo("实际处理器: {}", handler.getClass().getName());

        // 5. 拉取待处理任务
        List<T> tasks = handler.fetchTasks(param);

        // 6. 任务过滤（按 Filter 顺序执行）
        if (CollUtil.isNotEmpty(fetchFilters)) {
            for (TaskFetchFilter<T> filter : fetchFilters) {
                tasks = filter.filter(tasks, param);
            }
        }

        // 7. 触发拉取完成事件
        final List<T> fetchedTasks = tasks;
        fireEvent(l -> l.onTasksFetched(fetchedTasks, param));

        if (CollUtil.isEmpty(fetchedTasks)) {
            schedulerAdapter.logInfo("待处理任务为空");
            return;
        }
        schedulerAdapter.logInfo("待处理任务数量: {}", fetchedTasks.size());

        // 8. 批量执行（Handler 内部负责并发编排 + 状态管理 + 告警）
        handler.batchExecute(fetchedTasks, param);

        // 9. 触发批次完成事件
        fireEvent(l -> l.onBatchCompleted(fetchedTasks, null, param));

        schedulerAdapter.logInfo("SyncTaskEngine 执行完成");
    }

    // ==================== 事件触发 ====================

    @SuppressWarnings("unchecked")
    private void fireEvent(java.util.function.Consumer<TaskLifecycleListener<T>> action) {
        if (CollUtil.isEmpty(listeners)) {
            return;
        }
        for (TaskLifecycleListener<T> listener : listeners) {
            try {
                action.accept(listener);
            } catch (Exception e) {
                log.error("LifecycleListener 执行异常: {}", listener.getClass().getName(), e);
            }
        }
    }

    // ==================== Getter ====================

    public NotifyChannel getNotifyChannel() {
        return notifyChannel;
    }

    public LockService getLockService() {
        return lockService;
    }

    public TaskArchiveService<T> getArchiveService() {
        return archiveService;
    }

    // ==================== Builder ====================

    /**
     * 引擎构建器。
     */
    public static class Builder<T, C> {
        private SyncTaskHandlerRegistry<T, C> registry;
        private SchedulerAdapter schedulerAdapter;
        private List<SyncTaskParamValidator> validators = new ArrayList<>();
        private List<TaskFetchFilter<T>> fetchFilters = new ArrayList<>();
        private List<TaskLifecycleListener<T>> listeners = new ArrayList<>();
        private LockService lockService;
        private NotifyChannel notifyChannel;
        private TaskArchiveService<T> archiveService;

        public Builder<T, C> registry(SyncTaskHandlerRegistry<T, C> registry) {
            this.registry = registry;
            return this;
        }

        public Builder<T, C> schedulerAdapter(SchedulerAdapter schedulerAdapter) {
            this.schedulerAdapter = schedulerAdapter;
            return this;
        }

        public Builder<T, C> validators(List<SyncTaskParamValidator> validators) {
            this.validators = validators != null ? validators : new ArrayList<>();
            this.validators.sort(Comparator.comparingInt(v ->
                v instanceof Ordered ? ((Ordered) v).getOrder() : 0));
            return this;
        }

        public Builder<T, C> fetchFilters(List<TaskFetchFilter<T>> fetchFilters) {
            this.fetchFilters = fetchFilters != null ? fetchFilters : new ArrayList<>();
            this.fetchFilters.sort(Comparator.comparingInt(TaskFetchFilter::getOrder));
            return this;
        }

        public Builder<T, C> listeners(List<TaskLifecycleListener<T>> listeners) {
            this.listeners = listeners != null ? listeners : new ArrayList<>();
            return this;
        }

        public Builder<T, C> lockService(LockService lockService) {
            this.lockService = lockService;
            return this;
        }

        public Builder<T, C> notifyChannel(NotifyChannel notifyChannel) {
            this.notifyChannel = notifyChannel;
            return this;
        }

        public Builder<T, C> archiveService(TaskArchiveService<T> archiveService) {
            this.archiveService = archiveService;
            return this;
        }

        public SyncTaskEngine<T, C> build() {
            if (registry == null) {
                throw new IllegalStateException("SyncTaskHandlerRegistry is required");
            }
            if (schedulerAdapter == null) {
                throw new IllegalStateException("SchedulerAdapter is required");
            }
            return new SyncTaskEngine<>(this);
        }
    }

    /**
     * 有序接口（Validator 实现此接口来控制执行顺序）。
     */
    public interface Ordered {
        int getOrder();
    }

    /**
     * 参数提供者函数式接口。
     */
    @FunctionalInterface
    public interface SyncTaskParamSupplier {
        SyncTaskParam parse(String rawJson);
    }
}
