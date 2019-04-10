package hua.lee.plm.base;

import hua.lee.plm.bean.CP210xCommand;
import hua.lee.plm.bean.CommandRxWrapper;
import hua.lee.plm.task.CP210xCommTask;
import hua.lee.plm.task.CP210xProtocolTask;
import hua.lee.plm.vo.CommandVO;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * context
 *
 * @author lijie
 * @create 2019-01-08 12:10
 **/
public final class PLMContext {
    public static final byte TYPE_ACK = 0b00010000;
    public static final byte TYPE_N_ACK = 0b00100000;
    public static final byte TYPE_FUNC = 0b00000000;
    public static final byte TYPE_DATA = 0b00000001;
    public static final byte TYPE_CTL = 0b00000010;

    public static CP210xCommTask cp210xCommTask;
    public static CP210xProtocolTask cp210xProtocolTask;
    public static volatile ArrayBlockingQueue<CP210xCommand> cp210xRxQueue;
    public static volatile ArrayBlockingQueue<CP210xCommand> cp210xTxQueue;
    public static volatile ConcurrentHashMap<String, CP210xCommand> ackMap;
    public static volatile ConcurrentHashMap<String, CP210xCommand> nackMap;

    public static volatile CommunicatePort cp210xUsb;

    public static Map<String, CommandVO> cmdMap = new HashMap<>();
    public static Map<String, CommandRxWrapper> cmdWrapper = new HashMap<>();

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void d(String tag, String msg) {
        System.out.println(tag + " | " + msg);
    }

    public static void initServer() {
        if (cp210xCommTask == null && cp210xProtocolTask == null) {
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

    public static void closeServer() {
        if (cp210xCommTask != null) {
            cp210xCommTask.killComm();
            cp210xCommTask = null;
        }
        if (cp210xProtocolTask != null) {
            cp210xProtocolTask.killProtocol();
            cp210xProtocolTask = null;
        }
    }
}
