package com.struggle.oj.model.dto.question;

import lombok.Data;

/**
 * 题目配置
 */
@Data
public class JudgeConfig {
    /**
     * 事件限制（ms）
     */
    private long timeLimit;
    /**
     * 内存限制（KB）
     */
    private long memoryLimit;
    /**
     * 堆栈限制 (KB)
     */
    private long stackLimit;

}
