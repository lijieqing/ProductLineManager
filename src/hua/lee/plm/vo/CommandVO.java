package hua.lee.plm.vo;

import hua.lee.plm.type.CommandType;
import hua.lee.plm.type.ParamType;
import hua.lee.plm.type.ResultType;
import lee.hua.xmlparse.annotation.XmlAttribute;
import lee.hua.xmlparse.annotation.XmlBean;

/**
 * 命令VO类
 *
 * @author lijie
 * @create 2018-12-04 15:19
 **/
@XmlBean(name = "FMCommand")
public class CommandVO {
    /**
     * 指令ID
     */
    @XmlAttribute(name = "CommandId")
    private String cmd_ID;
    /**
     * 指令类型
     */
    @XmlAttribute(name = "CommandType")
    private String cmd_Type;
    /**
     * 指令携带参数
     */
    @XmlAttribute(name = "CommandParam")
    private String cmd_Param;
    /**
     * 指令参数类型
     */
    @XmlAttribute(name = "ParamType")
    private String mParamType;
    /**
     * 指令返回值类型
     */
    @XmlAttribute(name = "ResultType")
    private String mResultType;
    /**
     * 通讯间隔，单位 s
     */
    @XmlAttribute(name = "CommandPeriod")
    private Integer mPeriod;

    public String getCmd_ID() {
        return cmd_ID;
    }

    public void setCmd_ID(String cmd_ID) {
        this.cmd_ID = cmd_ID;
    }

    public CommandType getCmdType() {
        switch (cmd_Type) {
            case "SEND":
                return CommandType.Send;
            case "SLEEP":
                return CommandType.Sleep;
            default:
                return CommandType.Send;
        }
    }

    public void setCmd_Type(String cmd_Type) {
        this.cmd_Type = cmd_Type;
    }

    public ParamType getParamType() {
        switch (mParamType) {
            case "HEX":
                return ParamType.HEX;
            case "ASCII":
                return ParamType.String;
            default:
                return ParamType.VOID;
        }
    }

    public void setmParamType(String mParamType) {
        this.mParamType = mParamType;
    }

    public ResultType getResultType() {
        switch (mResultType) {
            case "HEX":
                return ResultType.HEX;
            case "ASCII":
                return ResultType.String;
            default:
                return ResultType.VOID;
        }
    }

    public void setmResultType(String mResultType) {
        this.mResultType = mResultType;
    }

    /**
     * 获取命令通讯间隔
     *
     * @return second
     */
    public Integer getPeriod() {
        return mPeriod;
    }

    public void setmPeriod(Integer mPeriod) {
        this.mPeriod = mPeriod;
    }

    public String getCmd_Param() {
        return cmd_Param;
    }

    public void setCmd_Param(String cmd_Param) {
        this.cmd_Param = cmd_Param;
    }

    @Override
    public String toString() {
        return "CommandVO{" +
                "cmd_ID='" + cmd_ID + '\'' +
                ", cmd_Type='" + cmd_Type + '\'' +
                ", mParamType='" + mParamType + '\'' +
                ", mResultType='" + mResultType + '\'' +
                ", mPeriod=" + mPeriod +
                '}';
    }
}
