package com.struggle.oj.judge;

import com.struggle.oj.model.entity.QuestionSubmit;

/**
 * @author Mr.Chen
 * 判题服务
 */
public interface JudgeService {
    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
