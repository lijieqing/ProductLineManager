package hua.lee.plm.bean;

import hua.lee.plm.base.Command;
import hua.lee.plm.base.ICommandWorker;
import hua.lee.plm.base.ICommunicate;
import hua.lee.plm.engine.CommandFactory;
import hua.lee.plm.type.ParamType;
import hua.lee.plm.vo.CommandVO;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 发送指令类
 *
 * @author lijie
 * @create 2018-10-30 17:16
 **/
public class RequestCommand extends Command implements ICommandWorker {
    private static final String PARAM_HEAD = "file:";
    private byte[] fileData = null;

    public RequestCommand(CommandVO vo) {
        setCommandVO(vo);
        setCMD_ID(mCommandID);
        if (mCommandParam != null) {
            switch (mParamType) {
                case String:
                    dataContent = mCommandParam.getBytes();
                    dataLen = dataContent.length;
                    break;
                case HEX:
                    //处理文件类数据传输
                    if (mCommandParam.contains(PARAM_HEAD)) {
                        try {
                            FileInputStream file = new FileInputStream(mCommandParam.replace(PARAM_HEAD, ""));
                            int len = file.available();
                            fileData = new byte[len];
                            file.read(fileData);
                        } catch (IOException e) {
                            System.out.println("文件读取异常 ：：" + e.toString());
                            e.printStackTrace();
                        }
                    } else {
                        //非文件类传输
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
        if (mParamType == ParamType.HEX) {
            if (fileData != null) {
                int frame = fileData.length / 64;
                if (frame > 0) {
                    //多帧数据，拆分为一帧最大64字节发送
                    for (int i = 0; i <= frame; i++) {
                        if (i != frame) {
                            dataLen = 64;
                        } else {
                            dataLen = fileData.length % 64;
                        }
                        dataContent = new byte[dataLen];
                        System.arraycopy(fileData, i * 64, dataContent, 0, dataLen);

                        frameNo = i;
                        frameSum = frame+1;
                        comm.send(this);
                    }
                } else {
                    //数据长度小于64字节
                    dataLen = fileData.length;
                    dataContent = fileData;
                    comm.send(this);
                }
            }
        } else {
            comm.send(this);
        }
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
