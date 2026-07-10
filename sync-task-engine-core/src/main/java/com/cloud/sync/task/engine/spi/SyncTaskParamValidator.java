package com.cloud.sync.task.engine.spi;

/**
 * 任务参数校验器接口。
 * <p>接入方实现此接口来定义各自项目的参数校验逻辑，
 * 引擎在解析参数后按顺序调用所有已注册的 Validator。</p>
 *
 * @author sync-task-engine
 * @date 2026-07-10
 */
@FunctionalInterface
public interface SyncTaskParamValidator {

    /**
     * 校验任务参数。
     *
     * @param param 解析后的参数配置
     * @throws ValidationException 校验不通过时抛出
     */
    void validate(SyncTaskParam param) throws ValidationException;

    /**
     * 校验异常。
     */
    class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }
}
