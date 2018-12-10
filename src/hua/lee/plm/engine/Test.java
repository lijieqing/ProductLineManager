package hua.lee.plm.engine;


import hua.lee.plm.base.DataReceivedCallback;
import hua.lee.plm.bean.MultiDataFrame;
import hua.lee.plm.bean.RequestCommand;
import hua.lee.plm.bean.SingleDataFrame;
import hua.lee.plm.vo.CommandListVO;
import hua.lee.plm.vo.CommandVO;

import java.io.IOException;
import java.util.Arrays;

/**
 * test
 *
 * @author lijie
 * @create 2018-10-30 15:30
 **/
public class Test implements DataReceivedCallback {


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        CommandManager manager = new CommandManager();
        manager.addDataReceivedListener(new DataReceivedCallback() {
            @Override
            public void onSingleDataReceived(byte[] data) {
                SingleDataFrame singleDataFrame = new SingleDataFrame(data);
                System.out.println(singleDataFrame.getCommandID() + " onReceived data ::: " + singleDataFrame.getCommandResult());
            }

            @Override
            public void onMultiDataReceived(byte[] data) {
                try {
                    System.out.println("received new MultiData ::: " + Arrays.toString(data));
                    new MultiDataFrame(data).generateFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //初始化串口服务
        manager.initServer();
        //初始化指令集
        CommandListVO volist = CommandFactory.readConfig("/Users/lijie/Desktop/COM.xml");

        while (volist.getmRun().toUpperCase().equals("T")) {
            for (CommandVO commandVO : volist.getCommandList()) {
                RequestCommand sc = new RequestCommand(commandVO);
                manager.addCmdToSendPool(sc);
                Thread.sleep(500);
            }

            Thread.sleep(1000 * 6);

            volist = CommandFactory.readConfig("/Users/lijie/Desktop/COM.xml");
        }

        manager.closeServer();
        System.out.println("close port");

    }

    @Override
    public void onSingleDataReceived(byte[] data) {

    }

    @Override
    public void onMultiDataReceived(byte[] data) {

    }
}
