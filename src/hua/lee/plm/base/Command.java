package hua.lee.plm.base;

import hua.lee.plm.type.CommandType;
import hua.lee.plm.type.ParamType;
import hua.lee.plm.type.ResultType;
import hua.lee.plm.vo.CommandVO;

/**
 * 指令基础类
 *
 * @author lijie
 * @create 2018-10-30 14:52
 **/
public abstract class Command {
    public static final int FRAME_HEAD = 0x79;
    public static final int FRAME_TAIL = 0xFE;
    /**
     * 普通帧类型
     */
    protected static final int NORMAL_TYPE = 0x00;
    /**
     * ACK帧类型
     */
    protected static final int ACK_TYPE = 0x10;
    /**
     * NACK帧类型
     */
    protected static final int NACK_TYPE = 0x20;
    /**
     * 指令 ID
     */
    protected String mCommandID;
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

    private CommandVO vo;

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

    public abstract byte[] transToByte();


    public Command() {
        //System.out.println(" command ");
    }

    public String getCommandID() {
        return mCommandID;
    }

    public CommandType getCommandType() {
        return mCommandType;
    }
    public void setCommandVO(CommandVO vo){
        if (vo==null) {
            return;
        }
        this.vo = vo;
        mCommandID = vo.getCmd_ID();
        mCommandType = vo.getCmdType();
        mParamType = vo.getParamType();
        mResultType = vo.getResultType();
        mCommandParam = vo.getCmd_Param();
    }

    public String getCommandResult() {
        return mCommandResult;
    }
}
