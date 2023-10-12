package com.struggle.oj.judge;

import com.struggle.oj.judge.strategy.DefaultJudgeStrategy;
import com.struggle.oj.judge.strategy.JavaLanguageJudgeStrategy;
import com.struggle.oj.judge.strategy.JudgeContext;
import com.struggle.oj.judge.strategy.JudgeStrategy;
import com.struggle.oj.judge.codesandbox.model.JudgeInfo;
import com.struggle.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Chen
 * 判题（管理简化调用）
 */
@Service
public class JudgeManager {
    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
