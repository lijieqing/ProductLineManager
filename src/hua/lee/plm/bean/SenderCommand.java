package hua.lee.plm.bean;

import hua.lee.plm.base.Command;
import hua.lee.plm.base.ICommandWorker;
import hua.lee.plm.base.ICommunicate;
import hua.lee.plm.engine.CommandFactory;
import hua.lee.plm.vo.CommandVO;

import java.util.Arrays;

/**
 * 发送指令类
 *
 * @author lijie
 * @create 2018-10-30 17:16
 **/
public class SenderCommand extends Command implements ICommandWorker {

    public SenderCommand(CommandVO vo) {
        setCommandVO(vo);
        setCMD_ID(mCommandID);
        if (mCommandParam != null) {
            switch (mParamType) {
                case String:
                    dataContent = mCommandParam.getBytes();
                    dataLen = dataContent.length;
                    break;
                case HEX:
                    if (mCommandParam.length() % 2 != 0) {
                        System.out.println("数据帧参数异常 ：：：hex param must be an even number==>" + mCommandParam);
                    } else {
                        dataLen = mCommandParam.length() / 2;
                        dataContent = new byte[dataLen];
                        for (int i = 0; i < dataLen; i++) {
                            String val = "" + mCommandParam.charAt(i) + mCommandParam.charAt(i + 1);
                            dataContent[i] = (byte) Integer.parseInt(val, 16);
                        }
                    }
                    break;
            }
            System.out.println("set data content :: " + Arrays.toString(dataContent));
        }
        switch (mCommandType) {
            case Send:
                frameType = NORMAL_TYPE;
                break;
            case ACK:
                frameType = ACK_TYPE;
                break;
            case NACK:
                frameType = NACK_TYPE;
                break;
        }
    }

    @Override
    public boolean worker(ICommunicate comm) {
        comm.send(this);
        return true;
    }

    @Override
    public byte[] transToByte() {
        byte[] frame = new byte[getFrameLen()];
        frame[0] = FRAME_HEAD;
        frame[1] = frameType;
        frame[2] = (byte) frameCMD_ID[0];
        frame[3] = (byte) frameCMD_ID[1];
        frame[4] = (byte) dataLen;
        for (byte i = 0; i < dataLen; i++) {
            frame[5 + i] = dataContent[i];
            System.out.println("package data content :: " + dataContent[i]);
        }
        int frameTailPos = getFrameLen() - 1;
        int crcPos = getFrameLen() - 2;
        int frameSumPos = getFrameLen() - 3;
        int frameNoPos = getFrameLen() - 4;
        frame[frameNoPos] = (byte) frameNo;
        frame[frameSumPos] = (byte) frameSum;
        frame[crcPos] = (byte) CommandFactory.calCRC(frame);
        frame[frameTailPos] = (byte) FRAME_TAIL;
        return frame;
    }
}
