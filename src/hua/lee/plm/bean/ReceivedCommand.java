package hua.lee.plm.bean;

import hua.lee.plm.base.Command;
import hua.lee.plm.type.CommandType;

import static hua.lee.plm.engine.CommandFactory.calCRC;
import static hua.lee.plm.engine.CommandFactory.getCommadVO;

/**
 * 命令帧接收对象
 *
 * @author lijie
 * @create 2018-12-04 10:02
 **/
public class ReceivedCommand extends Command {
    private byte[] originFrame;

    public ReceivedCommand(byte[] originFrame) {
        this.originFrame = originFrame;
        if ((byte) calCRC(originFrame) != originFrame[originFrame.length - 2]) {
            System.out.println("帧解析错误，CRC is not correct calCRC=" + calCRC(originFrame) + "||| frameCRC=" + originFrame[originFrame.length - 2]);
        }
        frameToCommand();
    }

    /**
     * 字节数据转换为 Command 对象信息
     */
    private void frameToCommand() {
        //解析 CMD ID
        String l = Integer.toHexString(originFrame[2] & 0xff);
        String r = Integer.toHexString(originFrame[3] & 0xff);
        mCommandID = l + r;
        //获取指令返回数据类型
        System.out.println(mCommandID);
        mResultType = getCommadVO(mCommandID).getResultType();
        //解析返回数据
        StringBuilder param = new StringBuilder();
        int dataLen = originFrame[4];
        for (int i = 5; i < 5 + dataLen; i++) {
            param.append(Integer.toHexString(originFrame[i]));
        }
        mCommandParam = param.toString();
        //根据不同返回值类型进行转码
        byte cmdtype = originFrame[1];
        switch (cmdtype) {
            case NORMAL_TYPE:
                mCommandType = CommandType.Send;
                break;
            case ACK_TYPE:
                mCommandType = CommandType.ACK;
                break;
            case NACK_TYPE:
                mCommandType = CommandType.NACK;
                break;
        }
        if (originFrame[4] > 0) {
            byte[] data = new byte[dataLen];
            System.arraycopy(originFrame, 5, data, 0, dataLen);
            switch (mResultType) {
                case HEX:
                    StringBuilder sb = new StringBuilder();
                    for (byte b : data) {
                        sb.append(Integer.toHexString(b & 0xff));
                    }
                    mCommandResult = sb.toString();
                    break;
                case String:
                    mCommandResult = new String(data);
                    break;
                case VOID:
                    break;
            }
            System.out.println("received param ====> " + mCommandResult);
        } else {
            System.out.println("no data");
        }
    }

    @Override
    public byte[] transToByte() {
        return originFrame;
    }

    public byte[] generateACKCMD() {
        byte[] ack = new byte[9];
        ack[0] = (byte) FRAME_HEAD;
        ack[1] = ACK_TYPE;
        ack[2] = originFrame[2];
        ack[3] = originFrame[3];
        ack[4] = 0x00;
        ack[5] = 0x00;
        ack[6] = 0x01;
        ack[7] = (byte) calCRC(ack);
        ack[8] = (byte) FRAME_TAIL;
        return ack;
    }
}
