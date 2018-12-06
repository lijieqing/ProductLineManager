package hua.lee.plm.engine;


import hua.lee.plm.base.DataReceivedCallback;
import hua.lee.plm.bean.ReceivedCommand;
import hua.lee.plm.bean.SenderCommand;
import hua.lee.plm.vo.CommandListVO;
import hua.lee.plm.vo.CommandVO;

import java.io.IOException;

/**
 * test
 *
 * @author lijie
 * @create 2018-10-30 15:30
 **/
public class Test implements DataReceivedCallback {


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        CommandManager manager = new CommandManager();
        manager.addDataReceivedListener(data -> {
            ReceivedCommand receivedCommand = new ReceivedCommand(data);
            System.out.println(receivedCommand.getCommandID() + " onReceived data ::: " + receivedCommand.getCommandResult());
        });
        //初始化串口服务
        manager.initServer();
        //初始化指令集
        CommandListVO volist = CommandFactory.readConfig("/Users/lijie/Desktop/COM.xml");

        while (volist.getmRun().toUpperCase().equals("T")) {
            for (CommandVO commandVO : volist.getCommandList()) {
                SenderCommand sc = new SenderCommand(commandVO);
                manager.addCmdToSendPool(sc);
                Thread.sleep(1000);
            }

            Thread.sleep(3000);
            volist = CommandFactory.readConfig("/Users/lijie/Desktop/COM.xml");
        }

        manager.closeServer();
        System.out.println("close port");

    }

    @Override
    public void onReceived(byte[] data) {

    }
}
