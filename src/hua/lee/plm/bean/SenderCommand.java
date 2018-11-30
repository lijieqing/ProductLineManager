package hua.lee.plm.bean;

import hua.lee.plm.base.Command;
import hua.lee.plm.base.ICommandWorker;
import hua.lee.plm.base.ICommunicate;
import hua.lee.plm.type.CommandType;
import hua.lee.plm.type.ParamType;
import hua.lee.plm.type.ResultType;

/**
 * 发送指令类
 *
 * @author lijie
 * @create 2018-10-30 17:16
 **/
public class SenderCommand extends Command implements ICommandWorker {

    private SenderCommand(Builder builder) {
        mCommandID = builder.mCommandID;
        mCommandDesc = builder.mCommandDesc;
        mCommandParam = builder.mCommandParam;
        mParamType = builder.mParamType;
        mResultType = builder.mResultType;
        mCommandType = builder.mCommandType;
    }

    @Override
    public boolean worker(ICommunicate comm) {
        comm.send(this);
        return true;
    }


    public static final class Builder {
        private int mCommandID;
        private String mCommandDesc;
        private String mCommandParam;
        private ParamType mParamType;
        private ResultType mResultType;
        private CommandType mCommandType;

        public Builder() {
        }

        public Builder mCommandID(int val) {
            mCommandID = val;
            return this;
        }

        public Builder mCommandDesc(String val) {
            mCommandDesc = val;
            return this;
        }

        public Builder mCommandParam(String val) {
            mCommandParam = val;
            return this;
        }

        public Builder mParamType(ParamType val) {
            mParamType = val;
            return this;
        }

        public Builder mResultType(ResultType val) {
            mResultType = val;
            return this;
        }

        public Builder mCommandType(CommandType val) {
            mCommandType = val;
            return this;
        }

        public SenderCommand build() {
            return new SenderCommand(this);
        }
    }
}
