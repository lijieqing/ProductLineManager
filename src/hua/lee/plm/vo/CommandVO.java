package hua.lee.plm.vo;

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
     * 指令参数类型
     */
    @XmlAttribute(name = "ParamType")
    private String mParamType;
    /**
     * 指令返回值类型
     */
    @XmlAttribute(name = "ResultType")
    private String mResultType;

    public String getCmd_ID() {
        return cmd_ID;
    }

    public void setCmd_ID(String cmd_ID) {
        this.cmd_ID = cmd_ID;
    }

    public String getCmd_Type() {
        return cmd_Type;
    }

    public void setCmd_Type(String cmd_Type) {
        this.cmd_Type = cmd_Type;
    }

    public String getmParamType() {
        return mParamType;
    }

    public void setmParamType(String mParamType) {
        this.mParamType = mParamType;
    }

    public String getmResultType() {
        return mResultType;
    }

    public void setmResultType(String mResultType) {
        this.mResultType = mResultType;
    }

    @Override
    public String toString() {
        return "CommandVO{" +
                "cmd_ID='" + cmd_ID + '\'' +
                ", cmd_Type=" + cmd_Type +
                ", mParamType='" + mParamType + '\'' +
                ", mResultType='" + mResultType + '\'' +
                '}';
    }
}
