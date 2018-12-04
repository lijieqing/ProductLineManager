package hua.lee.plm.engine;

import gnu.io.*;
import hua.lee.plm.base.Command;
import hua.lee.plm.base.ICommunicate;
import hua.lee.plm.bean.ReceivedCommand;
import hua.lee.plm.type.CommandType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * 通讯引擎
 *
 * @author lijie
 * @create 2018-10-30 18:23
 **/
public class CommunicateEngine implements ICommunicate {
    private static SerialPort port = null;
    private static OutputStream output;
    private static InputStream input;

    public CommunicateEngine() {
        initPort();
    }


    @Override
    public void initPort() {
        if (port != null) {
            System.out.println("已初始化串口：" + port.getName());
            return;
        }
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier cp = (CommPortIdentifier) ports.nextElement();
            System.out.println(cp.getName() + " :::: " + cp.toString());
            if (cp.getName().contains("tty.SLAB_USBtoUART")) {
                try {
                    //mac 第一次运行程序，需建立/var/lock 文件夹
                    //并且设置权限 chmod go+rwx
                    CommPort commPort = cp.open("/dev/tty.SLAB_USBtoUART", 50);
                    if (commPort instanceof SerialPort) {
                        port = (SerialPort) commPort;
                        port.setSerialPortParams(115200, SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                        input = port.getInputStream();
                        output = port.getOutputStream();
                        port.addEventListener(new SerialReader(input));
                        port.notifyOnDataAvailable(true);
                    }

                } catch (PortInUseException e) {
                    e.printStackTrace();
                    System.out.println(cp.getName() + " ::: init failed \n" + e.getMessage());
                } catch (UnsupportedCommOperationException | IOException | TooManyListenersException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void closePort() {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            input = null;
        }

        if (port != null) {
            port.close();
        }
    }

    /**
     * 初始化发送server
     */
    private void initSendServer() {

    }

    /**
     * 初始化接收线程
     */
    private void initReceiveServer() {

    }

    @Override
    public void send(Command cmd) {
        System.out.println(" usb ttl send cmd start ");
        byte[] data_byte = cmd.transToByte();
        new Thread(new SerialWriter(output, data_byte)).start();

        System.out.println(" usb ttl send cmd finish ");
    }


    /**
     * 串口读取对象
     */
    private static class SerialReader implements SerialPortEventListener {
        private InputStream ins;

        public SerialReader(InputStream ins) {
            this.ins = ins;
        }

        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            byte[] buffer = new byte[128];
            try {
                Thread.sleep(500);
                int l = ins.available();
                if (l > 0) {
                    ins.read(buffer);
                    byte[] data = new byte[l];
                    System.arraycopy(buffer, 0, data, 0, l);
                    //解析数据
                    CommandParser.parse(data);
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 串口输入对象
     */
    private static class SerialWriter implements Runnable {
        private OutputStream ops;
        private byte[] cmd;

        public SerialWriter(OutputStream ops, byte[] cmd) {
            this.ops = ops;
            this.cmd = cmd;
        }

        @Override
        public void run() {
            try {
                ops.write(cmd);
                ops.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 命令解析类
     */
    private static class CommandParser {
        static byte[] recData;
        static ReceivedCommand recCmd;
        static List<ReceivedCommand> recList = new ArrayList<>();

        private static void parse(byte[] data) {
            System.out.println("parse:: " + Arrays.toString(data));
            for (int i = 0; i < data.length; i++) {
                //检测到 head 开始组装
                if (data[i] == (byte) Command.FRAME_HEAD) {
                    int frameLen = 9 + data[i + 4];
                    //判断尾部位置
                    if (data[i + frameLen - 1] == (byte) Command.FRAME_TAIL) {
                        //创建填充数据接收区域
                        recData = new byte[frameLen];
                        System.arraycopy(data, i, recData, 0, frameLen);
                        //转换为接收指令对象
                        recCmd = new ReceivedCommand(recData);
                        //当收到数据帧时，回复 ACK
                        if (recCmd.getCommandType() == CommandType.Send) {
                            new Thread(new SerialWriter(output, recCmd.generateACKCMD())).start();
                        } else {
                            recList.add(recCmd);
                        }
                        System.out.println("rec List ====>" + recList.size());
                    }
                    i += frameLen;
                }
            }


        }
    }


}
