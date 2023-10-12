package com.struggle.oj.judge.strategy;

import com.struggle.oj.model.dto.question.JudgeCase;
import com.struggle.oj.judge.codesandbox.model.JudgeInfo;
import com.struggle.oj.model.entity.Question;
import com.struggle.oj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Chen
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private Question question;

    private List<JudgeCase> judgeCasesList;

    private QuestionSubmit questionSubmit;

}
