package hua.lee.plm.vo;

import lee.hua.xmlparse.annotation.XmlAttribute;
import lee.hua.xmlparse.annotation.XmlBean;
import lee.hua.xmlparse.annotation.XmlListNode;

import java.util.List;

/**
 * 命令集合VO 类
 *
 * @author lijie
 * @create 2018-12-04 15:47
 **/
@XmlBean(name = "FMCommandList")
public class CommandListVO {
    @XmlListNode(name = "FMCommand", nodeType = CommandVO.class)
    private List<CommandVO> commandList;

    @XmlAttribute(name = "Running")
    private String mRun;

    public List<CommandVO> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<CommandVO> commandList) {
        this.commandList = commandList;
    }

    public String getmRun() {
        return mRun;
    }

    public void setmRun(String mRun) {
        this.mRun = mRun;
    }
}
