package hua.lee.plm.bean;

import gnu.io.*;
import hua.lee.plm.base.CommunicatePort;

import java.io.IOException;
import java.util.Enumeration;

/**
 * @author lijie
 * @create 2019-02-22 13:33
 **/
public class CP2102CommunicatePort extends CommunicatePort {
    private static SerialPort port;

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
                        inputStream = port.getInputStream();
                        outputStream = port.getOutputStream();
                    }

                } catch (PortInUseException e) {
                    e.printStackTrace();
                    System.out.println(cp.getName() + " ::: init failed \n" + e.getMessage());
                } catch (UnsupportedCommOperationException | IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void closePort() {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            inputStream = null;
            outputStream = null;
        }

        if (port != null) {
            port.close();
            port = null;
        }
    }
}
