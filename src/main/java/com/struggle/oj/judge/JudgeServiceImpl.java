package com.struggle.oj.judge;

import cn.hutool.json.JSONUtil;
import com.struggle.oj.common.ErrorCode;
import com.struggle.oj.exception.BusinessException;
import com.struggle.oj.judge.codesandbox.CodeSandbox;
import com.struggle.oj.judge.codesandbox.CodeSandboxFactory;
import com.struggle.oj.judge.codesandbox.CodeSandboxProxy;
import com.struggle.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.struggle.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.struggle.oj.judge.strategy.DefaultJudgeStrategy;
import com.struggle.oj.judge.strategy.JavaLanguageJudgeStrategy;
import com.struggle.oj.judge.strategy.JudgeContext;
import com.struggle.oj.judge.strategy.JudgeStrategy;
import com.struggle.oj.model.dto.question.JudgeCase;
import com.struggle.oj.model.dto.question.JudgeConfig;
import com.struggle.oj.model.dto.questionsubmit.JudgeInfo;
import com.struggle.oj.model.entity.Question;
import com.struggle.oj.model.entity.QuestionSubmit;
import com.struggle.oj.model.enums.JudgeInfoMessageEnum;
import com.struggle.oj.model.enums.QuestionSubmitStatusEnum;
import com.struggle.oj.model.vo.QuestionSubmitVO;
import com.struggle.oj.service.QuestionService;
import com.struggle.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mr.Chen
 */
@Service
public class JudgeServiceImpl implements JudgeService {
    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1)传入题目的提交id，获取到对应的题目、提交信息（包含代码、编程语言等)
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if(questionSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if(question == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        }
        //2)如果不为等待状态
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题目正在判题中");

        }
        //3)更改判题（题目提交）的状态为“判题中"，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        //获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        //使用JSON解析工具将一个JSON字符串 judgeCaseStr 转换为一个 List<JudgeCase> 类型的对象 judgeCasesList
        List<JudgeCase> judgeCasesList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);

        List<String> inputList = judgeCasesList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeIno());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setQuestion(question);
        judgeContext.setJudgeCasesList(judgeCasesList);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        //6)修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionId);
        return questionSubmitResult;
    }
}
