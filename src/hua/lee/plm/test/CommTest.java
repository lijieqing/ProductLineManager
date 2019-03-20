package hua.lee.plm.test;

import hua.lee.plm.base.PLMContext;
import hua.lee.plm.bean.CommandTxWrapper;
import hua.lee.plm.task.CP210xCommTask;
import hua.lee.plm.task.CP210xProtocolTask;

import java.util.concurrent.ArrayBlockingQueue;

import static hua.lee.plm.base.PLMContext.*;

/**
 * 通讯测试
 *
 * @author lijie
 * @create 2019-03-20 13:14
 **/
public class CommTest {

    public static void main(String[] args) {
        initServer();
        PLMContext.sleep(3 * 1000);
        CommandTxWrapper txWrapper = CommandTxWrapper.initTX("1409",
                "/Users/lijie/Desktop/key22.bin", null,
                CommandTxWrapper.DATA_FILE, PLMContext.TYPE_CTL);
        txWrapper.send();


    }

    private static void initServer() {
        cp210xRxQueue = new ArrayBlockingQueue<>(1024);
        cp210xTxQueue = new ArrayBlockingQueue<>(1024);

        cp210xProtocolTask = new CP210xProtocolTask();
        cp210xCommTask = new CP210xCommTask();

        cp210xProtocolTask.taskInit();
        cp210xProtocolTask.start();

        cp210xCommTask.initTask();
        cp210xCommTask.start();

    }
}
