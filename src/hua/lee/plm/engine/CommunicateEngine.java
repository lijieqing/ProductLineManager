package hua.lee.plm.engine;

import gnu.io.*;
import hua.lee.plm.base.Command;
import hua.lee.plm.base.ICommunicate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

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
                if (input !=null){
                    input.close();
                }
                if (output !=null){
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
        int[] data = cmd.transToByte();
        byte[] data_byte = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            System.out.printf(Integer.toHexString(data[i]) + " | ");
            data_byte[i] = (byte) data[i];
        }
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
            byte[] buffer = new byte[1024];
            try {
                Thread.sleep(500);
                int l = ins.available();
                if (l > 0) {
                    byte[] res = new byte[l];
                    ins.read(buffer);

                    System.arraycopy(buffer, 0, res, 0, l);
                    for (byte re : res) {
                        int r = re & 0xff;
                        System.out.print("|" + Integer.toHexString(r) + " | ");
                    }
                    System.out.println();
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


}
