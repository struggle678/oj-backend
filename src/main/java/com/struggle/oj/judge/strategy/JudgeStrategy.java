package com.struggle.oj.judge.strategy;

import com.struggle.oj.judge.codesandbox.model.JudgeInfo;

/**
 * @author Mr.Chen
 * 判题策略
 */
public interface JudgeStrategy {
    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);

}
