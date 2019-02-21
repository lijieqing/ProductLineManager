package hua.lee.plm.bean;

import com.sun.istack.internal.NotNull;
import hua.lee.plm.base.PLMContext;
import hua.lee.plm.engine.CommandFactory;
import hua.lee.plm.engine.CommandServer;
import hua.lee.plm.vo.CommandVO;

import java.io.*;
import java.util.LinkedList;

/**
 * 命令发送包装类
 *
 * @author lijie
 * @create 2019-02-21 13:41
 **/
public class CommandTxWrapper {
    public static final int DATA_FILE = 0;
    public static final int DATA_STRING = 1;

    private String cmdID;
    private LinkedList<Command> cmdList = new LinkedList<>();
    private CommandVO cmdVO;
    private byte cmd_left;
    private byte cmd_right;
    private byte cmdType;

    public CommandTxWrapper(@NotNull String cmdID, String data, int dataType, int cmdType) {
        this.cmdID = cmdID.toUpperCase();
        cmd_left = Byte.parseByte(cmdID.substring(0, 2), 16);
        cmd_right = Byte.parseByte(cmdID.substring(2, 4), 16);
        this.cmdType = (byte) cmdType;

        if (data == null) {
            Command cmd = CommandFactory.generateCommandByID(cmdID);
            cmdList.add(cmd);
        } else {
            switch (dataType) {
                case DATA_FILE:
                    fileSplit(data);
                    break;
                case DATA_STRING:
                    stringSplit(data);
                    break;
            }
        }
    }

    public void send() {
        Command cmd;
        System.out.println("数据帧数量："+cmdList.size());
        while (cmdList.size() > 0) {
            cmd = cmdList.pop();
            //cmd.setCmdType(cmdType);

            PLMContext.commandServer.sendCommand(cmd);
            PLMContext.sleep(3000);
            Command ack = CommandServer.ackList.get(cmdID);
            if (ack == null) {
                System.out.println("未收到 ACK 回复");
                break;
            }
            CommandServer.ackList.remove(cmdID);
        }
        cmdList.clear();
    }


    private void stringSplit(String data) {
        byte[] datas = data.getBytes();
        bytesSplit(datas);
    }

    /**
     * 文件拆分为数据帧
     *
     * @param path 文件路径
     */
    private void fileSplit(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                InputStream is = new FileInputStream(file);
                int avail = is.available();
                byte[] fileData = new byte[avail];
                is.read(fileData);
                bytesSplit(fileData);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 字节数据拆分，满足一帧64字节数据
     *
     * @param datas 完整的字节数据
     */
    private void bytesSplit(byte[] datas) {
        if (datas.length > 64) {
            int sum = datas.length / 64;
            int suffix = datas.length % 64;
            if (suffix > 0) {
                sum += 1;
            }
            byte[] data;
            Command cmd;
            for (int i = 0; i < sum; i++) {
                if (i < (sum - 1)) {
                    data = new byte[64];
                    System.arraycopy(datas, i * 64, data, 0, 64);
                } else {
                    data = new byte[suffix];
                    System.arraycopy(datas, (i - 1) * 64, data, 0, suffix);
                }
                cmd = CommandFactory.generateCommandBySource(data, (byte) i, (byte) sum, cmd_left, cmd_right);
                cmdList.add(cmd);
            }

        } else {
            Command cmd = CommandFactory.generateCommandBySource(datas, (byte) 0, (byte) 1, cmd_left, cmd_right);
            cmdList.add(cmd);
        }
    }
}
