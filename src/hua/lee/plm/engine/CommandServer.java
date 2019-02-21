package hua.lee.plm.engine;

import hua.lee.plm.bean.Command;
import hua.lee.plm.bean.CommandRxWrapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * command server
 *
 * @author lijie
 * @create 2019-01-07 18:20
 **/
public class CommandServer {
    public static LinkedList<Command> sendList = new LinkedList<>();
    public static LinkedList<CommandRxWrapper> dataList = new LinkedList<>();
    public static Map<String, Command> ackList = new HashMap<>();

    private CommunicateEngine ce;
    private RxCommandTask rt;

    private static CommandRxWrapper wrapper = null;

    static void notifyDataReceived(byte[] data) {
        Command command = new Command(data);

        if (wrapper == null || !wrapper.isReceiving()) {
            wrapper = new CommandRxWrapper();
            wrapper.setCmdID(command.getCommandID());
//            wrapper = PLMContext.cmdWrapper.get(command.getCommandID());
//            if (wrapper == null) {
//                System.out.println("undefined command : ID = " + command.getCommandID());
//                return;
//            }
        }

        if ((command.getCmdNum() + 1) == command.getCmdSum()) {
            System.out.println("received last frame cmd data ,and cmd is is " + command.getCommandID());
            wrapper.addCommand(command);
            dataList.add(wrapper);
            wrapper.received();
            wrapper.onRxDataRec();
        } else {
            if (wrapper.getCmdID().equals(command.getCommandID())) {
                System.out.println("received multi cmd data ,and cmd id is " + command.getCommandID());

                wrapper.addCommand(command);
            } else {

                //wrapper = PLMContext.cmdWrapper.get(command.getCommandID());
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

    public void init() {
        ce = new CommunicateEngine();
        ce.start();

//        rt = new RxCommandTask();
//        rt.start();
    }

    public void sendCommand(Command cmd) {
        synchronized (this) {
            sendList.add(cmd);
//            int count = 0;
//            Command ack = null;
//            while (ack == null && count < 3) {
//                count++;
//                sendList.add(cmd);
//                ack = ackList.get(cmd.getCommandID());
//
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (ack != null) {
//                sendCommandFirst(new Command(CommandFactory.generateACKCMD(cmd.getCmdID_Left(), cmd.getCmdID_Right())));
//            }
        }

    }

    public void sendCommandFirst(Command cmd) {
        sendList.addFirst(cmd);
    }

    class RxCommandTask extends Thread {
        @Override
        public void run() {
            while (true) {

                //System.out.println("RxCommandTask :: size = "+dataList.size());
                while (dataList.size() > 0) {
                    CommandRxWrapper rxData = dataList.pop();
                    rxData.onRxDataRec();
                }

            }
        }
    }


}
