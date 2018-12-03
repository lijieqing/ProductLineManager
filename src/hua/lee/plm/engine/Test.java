package hua.lee.plm.engine;


import hua.lee.plm.bean.SenderCommand;
import hua.lee.plm.type.CommandType;
import hua.lee.plm.type.ParamType;
import hua.lee.plm.type.ResultType;

/**
 * test
 *
 * @author lijie
 * @create 2018-10-30 15:30
 **/
public class Test {


    public static void main(String[] args) {
        CommandManager manager = new CommandManager();
        SenderCommand.Builder builder = new SenderCommand.Builder();
        SenderCommand cmd_d3 = builder.mCommandID("14d3")
                .mCommandDesc("af pic")
                .mCommandParam("91")
                .mCommandType(CommandType.Send)
                .mParamType(ParamType.String)
                .mResultType(ResultType.HEX)
                .build();
        SenderCommand cmd_d4 = builder.mCommandID("14d4")
                .mCommandDesc("af pic")
                .mCommandParam("19")
                .mCommandType(CommandType.Send)
                .mParamType(ParamType.String)
                .mResultType(ResultType.HEX)
                .build();
        SenderCommand cmd_d5 = builder.mCommandID("14d5")
                .mCommandDesc("af pic")
                .mCommandParam("69")
                .mCommandType(CommandType.Send)
                .mParamType(ParamType.String)
                .mResultType(ResultType.HEX)
                .build();

        manager.initServer();
        new Thread(() -> {
            try {
                while (manager.isWatching()){
                    manager.addCmdToSendPool(cmd_d3);
                    Thread.sleep(1000*2);
                    manager.addCmdToSendPool(cmd_d5);
                    Thread.sleep(1000*15);
                    manager.addCmdToSendPool(cmd_d4);
                    Thread.sleep(1000*5);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            Thread.sleep(1000 * 60*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        manager.closeServer();
        System.out.println("close port");
    }
}
