package hua.lee.plm.test;

import hua.lee.plm.base.RxDataCallback;
import hua.lee.plm.bean.Command;
import hua.lee.plm.bean.CommandWrapper;
import hua.lee.plm.engine.CommandServer;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

import static hua.lee.plm.engine.CommandFactory.*;

/**
 * hdcp 读写测试
 *
 * @author lijie
 * @create 2019-02-18 13:48
 **/
public class HDCPTest {
    private static Command cmd1409;
    private static Command cmd1411;

    public static void main(String[] args) {
        MyCallback callback = new MyCallback();
        CommandServer server = new CommandServer();
        server.init();

        CommandWrapper.addRxDataCallBack("1409", callback);
        CommandWrapper.addRxDataCallBack("1411", callback);
        CommandWrapper.addRxDataCallBack("1475", callback);

        cmd1409 = generateCommandByID("1409");
        cmd1411 = generateCommandByID("1411");
        //server.sendCommand(generateCommandByID("1475"));
//        server.sendCommand();
        //server.sendCommand(generateCommandByID("1411"));
        new Thread() {
            @Override
            public void run() {
                int i = 0;
                while (i < 30) {
                    server.sendCommand(cmd1409);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    server.sendCommand(cmd1411);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
        }.start();
    }

    static class MyCallback implements RxDataCallback {

        @Override
        public void notifyDataReceived(String cmdID, byte[] data) {
            String path = "/Users/lijie/Desktop/TE/"+(cmdID.equals("1409")?"hdcp14":"hdcp22")+ new Date().getTime()+".bin";
            File file = new File(path);
            OutputStream outs = null;
            if (!file.exists()){
                try {
                    file.createNewFile();
                    outs = new FileOutputStream(file);
                    outs.write(data);
                    outs.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outs!=null){
                try {
                    outs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
