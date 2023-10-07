package com.struggle.oj.judge.codesandbox.impl;

import com.struggle.oj.judge.codesandbox.CodeSandbox;
import com.struggle.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.struggle.oj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * @author Mr.Chen
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        return null;
    }
}
