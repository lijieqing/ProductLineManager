package hua.lee.plm.test;

import hua.lee.plm.base.RxDataCallback;
import hua.lee.plm.bean.CommandWrapper;
import hua.lee.plm.engine.CommandServer;

import java.util.Arrays;

import static hua.lee.plm.engine.CommandFactory.*;

/**
 * hdcp 读写测试
 *
 * @author lijie
 * @create 2019-02-18 13:48
 **/
public class HDCPTest {
    public static void main(String[] args) {
        MyCallback callback = new MyCallback();
        CommandServer server = new CommandServer();
        server.init();

        CommandWrapper.addRxDataCallBack("1409",callback);
        CommandWrapper.addRxDataCallBack("1411",callback);
        CommandWrapper.addRxDataCallBack("1475",callback);

        //server.sendCommand(generateCommandByID("1475"));
        server.sendCommand(generateCommandByID("1409"));
        //server.sendCommand(generateCommandByID("1411"));
    }

    static class MyCallback implements RxDataCallback{

        @Override
        public void notifyDataReceived(String cmdID, byte[] data) {
            System.out.println(cmdID +" ||| "+ Arrays.toString(data));
        }
    }
}
