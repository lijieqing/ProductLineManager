package hua.lee.plm.engine;

import com.sun.istack.internal.NotNull;
import gnu.io.*;
import hua.lee.plm.base.Command;
import hua.lee.plm.base.DataReceivedCallback;
import hua.lee.plm.base.ICommunicate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static hua.lee.plm.engine.CommandFactory.*;

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
    private static List<DataReceivedCallback> receivedCallbackList = new ArrayList<>();

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
    public void addReceivedCallback(@NotNull DataReceivedCallback callback) {
        if (!receivedCallbackList.contains(callback)) {
            receivedCallbackList.add(callback);
        }
    }

    private static void notifyAll(byte[] singleData, byte[] multiData) {
        System.out.println("Communicate ==> notifyAll");
        for (DataReceivedCallback callback : receivedCallbackList) {
            if (singleData != null) {
                callback.onSingleDataReceived(singleData);
            }
            if (multiData != null) {
                callback.onMultiDataReceived(multiData);
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            byte[] buffer = new byte[1024];
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
        static byte[] multiData;
        static byte last_left_CmdID;
        static byte last_right_CmdID;

        private static void parse(byte[] data) {
            System.out.println("parse:: " + Arrays.toString(data));
            for (int i = 0; i < data.length; i++) {
                //检测到 head 开始组装
                if (data[i] == (byte) Command.FRAME_HEAD) {
                    int frameLen = 9 + data[i + 4];
                    //判断尾部位置
                    if (data[i + frameLen - 1] == (byte) Command.FRAME_TAIL) {
                        //创建填充数据接收区域
                        byte[] recData = new byte[frameLen];
                        System.arraycopy(data, i, recData, 0, frameLen);
                        //如果是单数据帧，且 CRC 校验正确
                        if (recData[1] == DATA_TYPE && recData[frameLen - 3] == 1
                                && (byte) calCRC(recData) == recData[frameLen - 2]) {
                            //System.out.println("origin rec data ::: " + Arrays.toString(recData));
                            new Thread(new SerialWriter(output, generateACKCMD(recData[2], recData[3]))).start();
                            CommunicateEngine.notifyAll(recData, null);
                        } else if (recData[1] == DATA_TYPE && recData[frameLen - 3] > 1) {
                            System.out.println("multi rec data ::: " + Arrays.toString(recData));
                            //指令 ID 不一致，但是数据帧帧序号为0，开始接收
                            if ((last_left_CmdID != recData[2] || last_right_CmdID != recData[3])
                                    && recData[frameLen - 4] == 0 && (byte) calCRC(recData) == recData[frameLen - 2]) {
                                multiData = new byte[0];
                                last_left_CmdID = recData[2];
                                last_right_CmdID = recData[3];
                            }
                            //帧命令 ID 与上次接收一致，则继续累加
                            if (last_left_CmdID == recData[2] && last_right_CmdID == recData[3]
                                    && (byte) calCRC(recData) == recData[frameLen - 2]) {
                                //数组扩展
                                multiData = Arrays.copyOf(multiData, multiData.length + recData[4]);
                                //计算数组拷贝起始位置，并开始拷贝
                                int startPos = multiData.length - recData[4];
                                System.arraycopy(recData, 5, multiData, startPos, recData[4]);
                                //当判断为最后一帧数据时，通知回调数据接收完成
                                if ((recData[frameLen - 4] + 1) == recData[frameLen - 3]) {
                                    System.out.println(multiData.length + " <==::::==> " + Arrays.toString(multiData));
                                    byte[] result = new byte[multiData.length + 2];
                                    result[0] = last_left_CmdID;
                                    result[1] = last_right_CmdID;
                                    last_left_CmdID = -1;
                                    last_right_CmdID = -1;

                                    System.arraycopy(multiData, 0, result, 2, multiData.length);
                                    CommunicateEngine.notifyAll(null, result);
                                }
                            }
                        }
                    }
                    //跳到帧尾部
                    i += frameLen - 1;
                }
            }
        }
    }


}
