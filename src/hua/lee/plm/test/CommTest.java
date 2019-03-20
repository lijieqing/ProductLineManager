package hua.lee.plm.test;

import hua.lee.plm.base.PLMContext;
import hua.lee.plm.bean.CP2102CommunicatePort;
import hua.lee.plm.bean.CommandTxWrapper;
import hua.lee.plm.engine.CP210xCommTask;
import hua.lee.plm.engine.CP210xProtocolTask;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;

import static hua.lee.plm.base.PLMContext.cp210xRxQueue;
import static hua.lee.plm.base.PLMContext.cp210xTxQueue;

/**
 * 通讯测试
 *
 * @author lijie
 * @create 2019-03-20 13:14
 **/
public class CommTest {
    @Test
    public void sendKey() {
        cp210xRxQueue = new ArrayBlockingQueue<>(1024);
        cp210xTxQueue = new ArrayBlockingQueue<>(1024);

        PLMContext.cp210xProtocolTask = new CP210xProtocolTask();
        PLMContext.cp210xCommTask = new CP210xCommTask();

        PLMContext.cp210xProtocolTask.taskInit();
        PLMContext.cp210xProtocolTask.start();

        PLMContext.cp210xCommTask.initTask();
        PLMContext.cp210xCommTask.start();

        CommandTxWrapper txWrapper = CommandTxWrapper.initTX("1409",
                "/Users/lijie/Desktop/key22.bin", null,
                CommandTxWrapper.DATA_STRING, PLMContext.TYPE_CTL);
        txWrapper.send();

        while (true){

        }

    }

    public static void main(String[] args) {


    }
}
