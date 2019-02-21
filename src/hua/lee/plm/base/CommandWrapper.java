package hua.lee.plm.base;

import hua.lee.plm.bean.Command;
import hua.lee.plm.vo.CommandVO;

import java.util.LinkedList;

/**
 * Command Wrapper
 *
 * @author lijie
 * @create 2019-02-21 17:02
 **/
public abstract class CommandWrapper {
    protected String cmdID = "";
    protected LinkedList<Command> cmdList;
    protected CommandVO cmdVO;
    protected byte cmd_left;
    protected byte cmd_right;
}
