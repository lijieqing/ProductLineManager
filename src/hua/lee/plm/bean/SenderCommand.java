package hua.lee.plm.bean;

import hua.lee.plm.base.Command;
import hua.lee.plm.base.ICommandWorker;
import hua.lee.plm.base.ICommunicate;
import hua.lee.plm.type.CommandType;
import hua.lee.plm.type.ParamType;
import hua.lee.plm.type.ResultType;

import java.util.Arrays;

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

        setCMD_ID(mCommandID);
        if (mCommandParam!=null){
            dataContent = mCommandParam.getBytes();
            System.out.println("set data content :: " + Arrays.toString(dataContent));
            dataLen = dataContent.length;
        }
    }

    @Override
    public boolean worker(ICommunicate comm) {
        comm.send(this);
        return true;
    }

    @Override
    public int[] transToByte() {
        int[] frame = new int[getFrameLen()];
        frame[0] = FRAME_HEAD;
        frame[1] = frameType;
        frame[2] = frameCMD_ID[0];
        frame[3] = frameCMD_ID[1];
        frame[4] = dataLen;
        for (byte i = 0; i < dataLen; i++) { ;
            frame[5+i] = dataContent[i];
            System.out.println("package data content :: " +dataContent[i]);
        }
        int frameTailPos = getFrameLen()-1;
        int crcPos = getFrameLen()-2;
        int frameSumPos = getFrameLen()-3;
        int frameNoPos = getFrameLen()-4;
        frame[frameNoPos] = frameNo;
        frame[frameSumPos] = frameSum;
        frame[crcPos] = calCRC();
        frame[frameTailPos] = FRAME_TAIL;
        return frame;
    }

    public static void main(String[] args) {
    }

    public static final class Builder {
        private String mCommandID;
        private String mCommandDesc;
        private String mCommandParam;
        private ParamType mParamType;
        private ResultType mResultType;
        private CommandType mCommandType;

        public Builder() {
        }

        public Builder mCommandID(String val) {
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
