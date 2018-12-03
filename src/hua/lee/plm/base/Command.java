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
    protected static final int FRAME_HEAD = 0x79;
    protected static final int FRAME_TAIL = 0xFE;
    /**
     * 普通帧类型
     */
    private static final int NORMAL_TYPE = 0x00;
    /**
     * ACK帧类型
     */
    private static final int ACK_TYPE = 0x10;
    /**
     * NACK帧类型
     */
    private static final int NACK_TYPE = 0x20;
    /**
     * 指令 ID
     */
    protected String mCommandID;
    /**
     * 指令描述
     */
    protected String mCommandDesc;
    /**
     * 指令参数
     */
    protected String mCommandParam;
    /**
     * 指令参数类型
     */
    protected ParamType mParamType;
    /**
     * 指令返回值类型
     */
    protected ResultType mResultType;
    /**
     * 指令类型
     */
    protected CommandType mCommandType;
    /**
     * 指令返回结果
     */
    protected String mCommandResult;
    /*打包数据帧*/
    /**
     * 帧类型
     */
    protected byte frameType = NORMAL_TYPE;
    /**
     * 命令 ID
     */
    protected int[] frameCMD_ID = new int[2];
    /**
     * 数据帧长度
     */
    protected int dataLen;
    /**
     * 数据帧
     */
    protected byte[] dataContent;
    /**
     * 当前数据帧序列号
     */
    protected int frameNo = 0x00;
    /**
     * 数据帧总数
     */
    protected int frameSum = 0x01;

    /**
     * 计算 CRC 校验值
     *
     * @return crc
     */
    protected int calCRC() {
        int sum = frameType + frameCMD_ID[0] + frameCMD_ID[1] + dataLen;

        for (byte data : dataContent) {
            sum += data;
        }
        sum += frameNo;
        sum += frameSum;

        if (sum > 0x100) {
            sum = sum % 0x100;
        }

        return (0x100 - sum);
    }

    /**
     * 获取帧长度
     * 9个固定位加上数据长度
     *
     * @return len
     */
    protected int getFrameLen() {
        return 9 + dataLen;
    }

    /**
     * 设置命令 ID
     *
     * @param cmd_id
     */
    public void setCMD_ID(String cmd_id) {
        if (cmd_id.trim().length() != 4) {
            throw new RuntimeException("cmd ID is error ==>" + cmd_id);
        }
        String left = cmd_id.substring(0, 2);
        String right = cmd_id.substring(2, 4);
        int l = Integer.parseInt(left, 16);
        int r = Integer.parseInt(right, 16);
        frameCMD_ID[0] = l;
        frameCMD_ID[1] = r;
    }

    public abstract int[] transToByte();


    public Command() {
        System.out.println(" command ");
    }


}
