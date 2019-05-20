package hua.lee.plm.bean;


import com.sun.istack.internal.Nullable;
import hua.lee.plm.base.PLMContext;

import java.util.Arrays;

/**
 * command
 *
 * @author lijie
 * @create 2019-01-07 15:01
 **/
public class CP210xCommand {
    private byte cmdID_Left;
    private byte cmdID_Right;
    private byte cmdType;
    private byte dataLen;
    private byte[] data;
    private byte cmdNum;
    private byte cmdSum;

    public CP210xCommand(byte[] origin) {
        if ((origin.length >= 9) && origin[0] == (byte) 0x79 && origin[origin.length - 1] == (byte) 0xFE) {
            init(origin[2], origin[3], origin[1], origin[4], null, origin[origin.length - 4], origin[origin.length - 3]);
            if (origin[4] > 0) {
                int len = origin[4];
                data = new byte[len];
                System.arraycopy(origin, 5, data, 0, len);
            }
        } else {
            System.out.println("error cmd structure");
        }
    }

    public CP210xCommand(byte cmdID_Left, byte cmdID_Right, byte cmdType, byte dataLen, @Nullable byte[] data, byte cmdNum, byte cmdSum) {
        init(cmdID_Left, cmdID_Right, cmdType, dataLen, data, cmdNum, cmdSum);
    }

    /**
     * 计算 CRC 校验值
     *
     * @param originFrame 数据帧
     * @return CRC value
     */
    public static byte calCRC(byte[] originFrame) {
        int sum = 0;
        for (int i = 0; i < originFrame.length; i++) {
            if (i > 0 && i < originFrame.length - 2) {
                sum += (originFrame[i] & 0xff);
            }
        }
        if (sum > 0x100) {
            sum = sum % 0x100;
        }
        return (byte) (0x100 - sum);
    }

    /**
     * 计算 CRC 校验值
     *
     * @param originFrame 数据帧
     * @return CRC value
     */
    public static byte calCRC(byte[] originFrame, int startPos, int len) {
        int sum = 0;
        for (int i = startPos; i < len; i++) {
            if (i > 0 && i < len - 2) {
                sum += (originFrame[i] & 0xff);
            }
        }
        if (sum > 0x100) {
            sum = sum % 0x100;
        }
        return (byte) (0x100 - sum);
    }

    /**
     * generate ACK CMD
     *
     * @param isAck true is ack,false is n-ack
     * @param cmdID command id
     * @return command
     */
    public static CP210xCommand generateACKCMD(boolean isAck, String cmdID) {
        if (cmdID.length() != 4) {
            if (cmdID.length() < 4) {
                int add = 4 - cmdID.length();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < add; i++) {
                    sb.append("0");
                }
                sb.append(cmdID);
                cmdID = sb.toString();
            } else {
                throw new IllegalArgumentException("cmd id len != 4, cmdid = " + cmdID);
            }
        }
        byte[] ack = new byte[9];
        ack[0] = (byte) 0x79;
        if (isAck) {
            ack[1] = PLMContext.TYPE_ACK;
        } else {
            ack[1] = PLMContext.TYPE_N_ACK;
        }
        byte cmd_left = (byte) Integer.parseInt(cmdID.substring(0, 2), 16);
        byte cmd_right = (byte) Integer.parseInt(cmdID.substring(2, 4), 16);
        ack[2] = cmd_left;
        ack[3] = cmd_right;
        ack[4] = 0x00;
        ack[5] = 0x00;
        ack[6] = 0x01;
        ack[7] = calCRC(ack);
        ack[8] = (byte) 0xFE;
        return new CP210xCommand(ack);
    }

    /**
     * generate ACK CMD
     *
     * @param cmdID command id
     * @return command
     */
    public static byte[] generateACKCMD(boolean isAck, byte... cmdID) {
        byte[] ack = new byte[9];
        ack[0] = (byte) 0x79;
        if (isAck) {
            ack[1] = PLMContext.TYPE_ACK;
        } else {
            ack[1] = PLMContext.TYPE_N_ACK;
        }
        ack[2] = cmdID[0];
        ack[3] = cmdID[1];
        ack[4] = 0x00;
        ack[5] = 0x00;
        ack[6] = 0x01;
        ack[7] = calCRC(ack);
        ack[8] = (byte) 0xFE;
        return ack;
    }

    public static CP210xCommand generateCommandByID(String cmdID) {
        if (cmdID.length() != 4) {
            return null;
        } else {
            byte cmd_left = (byte) Integer.parseInt(cmdID.substring(0, 2), 16);
            byte cmd_right = (byte) Integer.parseInt(cmdID.substring(2, 4), 16);
            return new CP210xCommand(cmd_left, cmd_right, PLMContext.TYPE_FUNC, (byte) 0, null, (byte) 0, (byte) 1);
        }
    }

    public static CP210xCommand generateCommandBySource(byte[] data, byte num, byte sum, byte... cmdID) {
        return new CP210xCommand(cmdID[0], cmdID[1], PLMContext.TYPE_FUNC, (byte) data.length, data, num, sum);
    }

    private void init(byte cmdID_Left, byte cmdID_Right, byte cmdType, byte dataLen, @Nullable byte[] data, byte cmdNum, byte cmdSum) {
        this.cmdID_Left = cmdID_Left;
        this.cmdID_Right = cmdID_Right;
        this.cmdType = cmdType;
        this.dataLen = dataLen;
        this.data = data;
        this.cmdNum = cmdNum;
        this.cmdSum = cmdSum;
    }

    public byte getCmdNum() {
        return cmdNum;
    }

    public byte getCmdSum() {
        return cmdSum;
    }

    public byte getCmdID_Left() {
        return cmdID_Left;
    }

    public byte getCmdID_Right() {
        return cmdID_Right;
    }

    public byte getCmdType() {
        return cmdType;
    }

    public void setCmdType(byte cmdType) {
        this.cmdType = cmdType;
    }

    public byte getDataLen() {
        return dataLen;
    }

    public byte[] getData() {
        return data;
    }

    public String getCommandID() {
        return Integer.toHexString((cmdID_Left << 8) + (cmdID_Right & 0xff)).toUpperCase();
    }

    /**
     * 转换为原始字节数组
     *
     * @return byte[] 数组
     */
    public byte[] toByteArray() {
        int cmdLen = (this.getDataLen() & 0xff) + 9;
        byte[] send = new byte[cmdLen];

        send[0] = 0x79;
        send[1] = this.getCmdType();
        send[2] = this.getCmdID_Left();
        send[3] = this.getCmdID_Right();
        send[4] = this.getDataLen();

        if (this.getDataLen() > 0) {
            System.arraycopy(this.getData(), 0, send, 5, this.getDataLen());
        }
        send[cmdLen - 4] = this.getCmdNum();
        send[cmdLen - 3] = this.getCmdSum();
        send[cmdLen - 2] = calCRC(send);
        send[cmdLen - 1] = (byte) 0xFE;

        return send;
    }

    @Override
    public String toString() {
        return "CP210xCommand{" +
                "cmdID_Left=" + cmdID_Left +
                ", cmdID_Right=" + cmdID_Right +
                ", cmdType=" + cmdType +
                ", dataLen=" + dataLen +
                ", data=" + Arrays.toString(data) +
                ", cmdNum=" + (cmdNum & 0xff) +
                ", cmdSum=" + (cmdSum & 0xff) +
                '}';
    }
}
