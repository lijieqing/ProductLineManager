package hua.lee.plm.test;

import hua.lee.plm.base.PLMContext;
import hua.lee.plm.base.RxDataCallback;
import hua.lee.plm.bean.CommandRxWrapper;
import hua.lee.plm.bean.CommandTxWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * hdcp 读写测试
 *
 * @author lijie
 * @create 2019-02-18 13:48
 **/
public class HDCPTest {

    public static void main(String[] args) {
        MyCallback callback = new MyCallback();

        PLMContext.commandServer.init();

        CommandRxWrapper.addRxDataCallBack("1409", callback);
        CommandRxWrapper.addRxDataCallBack("1411", callback);
        CommandRxWrapper.addRxDataCallBack("1475", callback);

    }

    static class MyCallback implements RxDataCallback {

        @Override
        public void notifyDataReceived(String cmdID, byte[] data) {
            System.out.println("received cmd id = " + cmdID);
            System.out.println("received data  = " + new String(data));
            if (cmdID.equals("1409")) {
                //CommandTxWrapper txWrapper = new CommandTxWrapper(cmdID, "/Users/lijie/Desktop/key22.bin", CommandTxWrapper.DATA_FILE);
                //txWrapper.send();
            }
        }

        private void generateFile(String cmdID, byte[] data) {
            String path = "/Users/lijie/Desktop/TE/" + (cmdID.equals("1409") ? "hdcp14" : "hdcp22") + new Date().getTime() + ".bin";
            File file = new File(path);
            OutputStream outs = null;
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    outs = new FileOutputStream(file);
                    outs.write(data);
                    outs.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outs != null) {
                try {
                    outs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
