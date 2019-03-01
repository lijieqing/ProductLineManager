package hua.lee.plm.engine;

import hua.lee.plm.bean.Command;
import hua.lee.plm.bean.CommandRxWrapper;
import hua.lee.plm.factory.IOFactory;

import java.util.*;

import static hua.lee.plm.factory.CommandFactory.*;


/**
 * command server
 *
 * @author lijie
 * @create 2019-01-07 18:20
 **/
public class CommandServer {
    public static volatile LinkedList<Command> sendList = new LinkedList<>();
    public static LinkedList<CommandRxWrapper> dataList = new LinkedList<>();
    public static volatile Map<String, Command> ackList = new HashMap<>();
    private static CommandRxWrapper wrapper = null;
    private CommunicateEngine ce;
    private Timer heartBeat;

    static void notifyDataReceived(byte[] data) {
        Command command = new Command(data);

        if (wrapper == null || !wrapper.isReceiving()) {
            wrapper = new CommandRxWrapper();
            wrapper.setCmdID(command.getCommandID());
        }

        if ((command.getCmdNum() + 1) == command.getCmdSum()) {
            System.out.println("received last frame cmd data ,and cmd is is " + command.getCommandID());
            wrapper.addCommand(command);
            dataList.add(wrapper);
            wrapper.received();
        } else {
            if (wrapper.getCmdID().equals(command.getCommandID())) {
                System.out.println("received multi cmd data ,and cmd id is " + command.getCommandID());
                wrapper.addCommand(command);
            } else {
                if (wrapper == null) {
                    System.out.println("undefined command : ID = " + command.getCommandID());
                } else {
                    System.out.println("received new cmd id " + command.getCommandID());
                    wrapper.addCommand(command);
                    wrapper.setCmdID(command.getCommandID());
                }
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

    public void sendCommandFirst(Command cmd) {
        sendList.addFirst(cmd);
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
