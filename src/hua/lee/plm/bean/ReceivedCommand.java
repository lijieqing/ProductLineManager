package hua.lee.plm.bean;

import hua.lee.plm.base.Command;
import hua.lee.plm.type.CommandType;

import java.util.Arrays;

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
        if (calCRC(originFrame) != originFrame[originFrame.length - 2]) {
            System.out.println("帧解析错误，CRC is not correct calCRC=" + calCRC(originFrame) + "||| frameCRC=" + originFrame[originFrame.length - 2]);
        }
        frameToCommand();
    }

    /**
     * 字节数据转换为 Command 对象信息
     */
    private void frameToCommand() {
        System.out.println("frameToCommand -> received data :: " + Arrays.toString(originFrame));
        String l = Integer.toHexString(originFrame[2]);
        String r = Integer.toHexString(originFrame[3]);
        mCommandID = l + r;
        StringBuilder param = new StringBuilder();
        int dataLen = originFrame[4];
        for (int i = 5; i < 5 + dataLen; i++) {
            param.append(Integer.toHexString(originFrame[i]));
        }
        mCommandParam = param.toString();
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
    }

    @Override
    protected int calCRC(byte[] originFrame) {
        int sum = 0;
        for (int i = 0; i < originFrame.length; i++) {
            if (i > 0 && i < originFrame.length - 2) {
                sum += (originFrame[i] & 0xff);
            }
        }
        if (sum > 0x100) {
            sum = sum % 0x100;
        }
        return 0x100 - sum;
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
