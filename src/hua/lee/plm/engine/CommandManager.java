package hua.lee.plm.engine;

import hua.lee.plm.base.ICommandWorker;
import hua.lee.plm.base.ICommunicate;
import hua.lee.plm.bean.SenderCommand;
import hua.lee.plm.type.CommandType;
import hua.lee.plm.type.ParamType;
import hua.lee.plm.type.ResultType;

import java.util.ArrayList;
import java.util.List;

/**
 * 命令管理类
 *
 * @author lijie
 * @create 2018-10-30 16:56
 **/
public class CommandManager {
    private List<SenderCommand> cmdList = new ArrayList<>();
    private ICommunicate comm = new CommunicateEngine();

    private void serialRun() {
        boolean pass = true;
        while (cmdList.size() > 0 && pass) {
            int pos = cmdList.size() - 1;
            ICommandWorker worker = cmdList.get(pos);
            pass = worker.worker(comm);

            cmdList.remove(pos);
        }
    }

    public void addCmdToSendPool(SenderCommand cmd){
        cmdList.add(cmd);
    }

    public void initCommand() {
        SenderCommand.Builder builder = new SenderCommand.Builder();
        SenderCommand cmd = builder.mCommandID(0x14AA)
                .mCommandDesc("14aa")
                .mCommandParam("param")
                .mCommandType(CommandType.Send)
                .mParamType(ParamType.String)
                .mResultType(ResultType.HEX)
                .build();
        cmdList.add(cmd);
        cmd = builder.mCommandID(0x14AB)
                .mCommandDesc("14ab")
                .mCommandParam("param")
                .mCommandType(CommandType.Send)
                .mParamType(ParamType.String)
                .mResultType(ResultType.HEX)
                .build();
        cmdList.add(cmd);
    }
}
