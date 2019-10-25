package hua.lee.plm.test;

import hua.lee.plm.base.PLMContext;
import hua.lee.plm.bean.CommandTxWrapper;

/**
 * 通讯测试
 *
 * @author lijie
 * @create 2019-03-20 13:14
 **/
public class CommTest {

    public static void main(String[] args) {
//        initServer();

//        writeKey();
//        activeKey();

//        switchSources();
//        readIP();

//        readNode();

//        byte a = 28;
//        byte b = 68;
//
//        System.out.println(a * 256 + b);

        //标准是>=4800

        String mac = "08:EB:29:CE:9F:C6";

        if (mac.matches("^[0-9A-Z]{2}:[0-9A-Z]{2}:[0-9A-Z]{2}:[0-9A-Z]{2}:[0-9A-Z]{2}:[0-9A-Z]{2}$")) {
            System.out.println(mac);
        }

    }

    private static void readNode() {
        PLMContext.sleep(3 * 1000);
        CommandTxWrapper txWrapper = CommandTxWrapper.initTX("14E3",
                "3,8", null,
                CommandTxWrapper.DATA_STRING, PLMContext.TYPE_CTL);
        txWrapper.send();
    }

    private static void switchSources() {
        PLMContext.sleep(3 * 1000);
        CommandTxWrapper txWrapper = CommandTxWrapper.initTX("112d",
                "0", null,
                CommandTxWrapper.DATA_STRING, PLMContext.TYPE_CTL);
        txWrapper.send();
        PLMContext.sleep(2 * 1000);
        txWrapper = CommandTxWrapper.initTX("142a",
                "MITEST_24G_1", null,
                CommandTxWrapper.DATA_STRING, PLMContext.TYPE_CTL);
        txWrapper.send();


    }


    private static void writeKey() {
        PLMContext.sleep(3 * 1000);
        CommandTxWrapper txWrapper = CommandTxWrapper.initTX("1408",
                "/Users/lijie/Desktop/hk14.bin", null,
                CommandTxWrapper.DATA_FILE, PLMContext.TYPE_CTL);
        txWrapper.send();

        PLMContext.sleep(5000);

        txWrapper = CommandTxWrapper.initTX("1410",
                "/Users/lijie/Desktop/hk22.bin", null,
                CommandTxWrapper.DATA_FILE, PLMContext.TYPE_CTL);

        txWrapper.send();
    }

    private static void activeKey() {
        PLMContext.sleep(3 * 1000);
        CommandTxWrapper txWrapper = CommandTxWrapper.initTX("140F",
                "", null,
                CommandTxWrapper.DATA_STRING, PLMContext.TYPE_CTL);
        txWrapper.send();
    }

    private static void readIP() {
        PLMContext.sleep(3 * 1000);
        CommandTxWrapper txWrapper = CommandTxWrapper.initTX("142c",
                "0", null,
                CommandTxWrapper.DATA_STRING, PLMContext.TYPE_CTL);
        txWrapper.send();
    }
}
