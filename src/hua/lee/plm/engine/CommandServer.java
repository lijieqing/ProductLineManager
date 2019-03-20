package hua.lee.plm.engine;

import hua.lee.plm.bean.CP210xCommand;
import hua.lee.plm.bean.Command;
import hua.lee.plm.bean.CommandRxWrapper;
import hua.lee.plm.factory.IOFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static hua.lee.plm.factory.CommandFactory.*;


/**
 * command server
 *
 * @author lijie
 * @create 2019-01-07 18:20
 **/
public class CommandServer {
    public static volatile ConcurrentLinkedQueue<Command> sendList = new ConcurrentLinkedQueue<>();
    public static LinkedList<CommandRxWrapper> dataList = new LinkedList<>();
    public static volatile ConcurrentHashMap<String, Command> ackList = new ConcurrentHashMap<>();
    private static CommandRxWrapper wrapper = null;
    private CommunicateEngine ce;
    private Timer heartBeat;

    static void notifyDataReceived(byte[] data) {
        CP210xCommand command = new CP210xCommand(data);
        //如果是 null 或者 未处在接收状态
        if (wrapper == null || !wrapper.isReceiving()) {
            if (wrapper == null) {
                wrapper = new CommandRxWrapper();
            }
            wrapper.startReceiving();
            wrapper.setCmdID(command.getCommandID());
        }
        //判断 cmd ID 是否一致
        if (wrapper.getCmdID().equals(command.getCommandID())) {
            //如果是最后一帧，接收完毕
            if ((command.getCmdNum() + 1) == command.getCmdSum()) {
                wrapper.addCommand(command);
                wrapper.received();
            } else {
                //继续接收
                wrapper.addCommand(command);
            }
        } else {
            // cmd ID 不一致，清空集合，重新接收
            wrapper.clearCommands();
            wrapper.startReceiving();
            wrapper.setCmdID(command.getCommandID());

            //如果是最后一帧，或者只有一帧，接收完毕
            if ((command.getCmdNum() + 1) == command.getCmdSum()) {
                wrapper.addCommand(command);
                wrapper.received();
            } else {
                //继续接收
                wrapper.addCommand(command);
            }
        }
    }

    /**
     * start communicate engine and heartbeat
     */
    public void init() {
        ce = new CommunicateEngine(IOFactory.initPort());
        ce.start();

        // heartBeat = new Timer();
        // heartBeat.schedule(new TimerTask() {
        //     @Override
        //     public void run() {
        //         sendCommand(generateHeartBeatCommand());
        //     }
        // }, 100, 6 * 1000);
    }

    public void sendCommand(Command cmd) {
        synchronized (this) {
            sendList.add(cmd);
        }

    }

    /**
     * close
     */
    public void close() {
        if (ce != null) {
            ce.killEngine();
        }
        if (heartBeat != null) {
            heartBeat.cancel();
        }
        IOFactory.resetPort();
    }
}
