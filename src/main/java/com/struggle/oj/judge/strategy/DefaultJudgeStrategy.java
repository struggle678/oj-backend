package com.struggle.oj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.struggle.oj.model.dto.question.JudgeCase;
import com.struggle.oj.model.dto.question.JudgeConfig;
import com.struggle.oj.model.dto.questionsubmit.JudgeInfo;
import com.struggle.oj.model.entity.Question;
import com.struggle.oj.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * @author Mr.Chen
 * 默认判题策略
 */
public class DefaultJudgeStrategy implements JudgeStrategy {
    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        long memory = judgeInfo.getMemory();
        long time = judgeInfo.getTime();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCasesList = judgeContext.getJudgeCasesList();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);
        //先判断沙箱执行的结果输出数量是否和预期输出数量相等
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        //依次判断每一项输出和预期输出是否相等
        for (int i = 0; i < judgeCasesList.size(); i++) {
            JudgeCase judgeCase = judgeCasesList.get(i);
            if (judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }
        //判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        //将一个JSON字符串 judgeConfigStr 转换为一个 JudgeConfig 类型的对象 judgeConfig
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        long needMemoryLimit = judgeConfig.getMemoryLimit();
        long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (time > needTimeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}