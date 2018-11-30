package hua.lee.plm.base;

import hua.lee.plm.type.CommandType;
import hua.lee.plm.type.ParamType;
import hua.lee.plm.type.ResultType;

/**
 * 指令基础类
 *
 * @author lijie
 * @create 2018-10-30 14:52
 **/
public abstract class Command {
    //指令 ID
    protected int mCommandID;
    //指令描述
    protected String mCommandDesc;
    //指令参数
    protected String mCommandParam;
    //指令帧内容
    protected String mCommandFrame;

    //指令参数类型
    protected ParamType mParamType;
    //指令返回值类型
    protected ResultType mResultType;
    //指令类型
    protected CommandType mCommandType;

    //指令返回结果
    protected String mCommandResult;

    public Command() {
        System.out.println("command ");
    }


}
