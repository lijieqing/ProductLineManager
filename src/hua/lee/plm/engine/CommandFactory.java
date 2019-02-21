package hua.lee.plm.engine;

import hua.lee.plm.bean.Command;
import hua.lee.plm.bean.CommandWrapper;
import hua.lee.plm.vo.CommandListVO;
import hua.lee.plm.vo.CommandVO;
import lee.hua.xmlparse.api.XMLAPI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static hua.lee.plm.base.PLMContext.cmdMap;
import static hua.lee.plm.base.PLMContext.cmdWrapper;

/**
 * Command 初始化 工厂类
 *
 * @author lijie
 * @create 2018-12-05 22:43
 **/
public final class CommandFactory {
    private CommandFactory() {
    }

    public static final byte CMD_FUNC = 0b00000000;
    public static final byte CMD_DATA = 0b00000001;
    public static final byte CMD_CONTROL = 0b00000010;
    public static final byte ACK_TYPE = 0b00010000;
    public static final byte NACK_TYPE = 0b00100000;

    /**
     * @param filePath "/Users/lijie/Desktop/COM.xml"
     * @return
     * @throws FileNotFoundException
     */
    public static CommandListVO readConfig(String filePath) throws IOException, ClassNotFoundException {
        return readConfig(filePath, false);
    }

    /**
     * read xml file config
     *
     * @param filePath path
     * @param append   false:clear and reload
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static CommandListVO readConfig(String filePath, boolean append) throws IOException, ClassNotFoundException {
        if (!append) {
            cmdMap.clear();
        }
        XMLAPI.setXmlBeanScanPackage("hua.lee.plm");
        CommandListVO res = (CommandListVO) XMLAPI.readXML(new FileInputStream(filePath));

        for (CommandVO commandVO : res.getCommandList()) {
            CommandWrapper wrapper = new CommandWrapper();
            cmdMap.put(commandVO.getCmd_ID().toUpperCase(), commandVO);

            wrapper.setCmdVO(commandVO);
            cmdWrapper.put(commandVO.getCmd_ID().toUpperCase(), wrapper);
        }
        return res;
    }

    /**
     * 获取 Command VO 对象
     *
     * @param cmdID
     * @return
     */
    public static CommandVO getCommandVO(String cmdID) {
        return cmdMap.get(cmdID);
    }


    /**
     * generate ACK CMD
     *
     * @param cmdID command id
     * @return command
     */
    public static byte[] generateACKCMD(byte... cmdID) {
        byte[] ack = new byte[9];
        ack[0] = (byte) 0x79;
        ack[1] = ACK_TYPE;
        ack[2] = cmdID[0];
        ack[3] = cmdID[1];
        ack[4] = 0x00;
        ack[5] = 0x00;
        ack[6] = 0x01;
        ack[7] = calCRC(ack);
        ack[8] = (byte) 0xFE;
        return ack;
    }

    /**
     * 计算 CRC 校验值
     *
     * @param originFrame 数据帧
     * @return CRC value
     */
    public static byte calCRC(byte[] originFrame) {
        int sum = 0;
        for (int i = 0; i < originFrame.length; i++) {
            if (i > 0 && i < originFrame.length - 2) {
                sum += (originFrame[i] & 0xff);
            }
        }
        if (sum > 0x100) {
            sum = sum % 0x100;
        }
        return (byte) (0x100 - sum);
    }

    /**
     * 计算 CRC 校验值
     *
     * @param originFrame 数据帧
     * @return CRC value
     */
    public static byte calCRC(byte[] originFrame, int startPos, int len) {
        int sum = 0;
        for (int i = startPos; i < len; i++) {
            if (i > 0 && i < len - 2) {
                sum += (originFrame[i] & 0xff);
            }
        }
        if (sum > 0x100) {
            sum = sum % 0x100;
        }
        return (byte) (0x100 - sum);
    }

    public static Command generateCommandByID(String cmdID) {
        if (cmdID.length() != 4) {
            return null;
        } else {
            byte cmd_left = Byte.parseByte(cmdID.substring(0, 2), 16);
            byte cmd_right = Byte.parseByte(cmdID.substring(2, 4), 16);
            return new Command(cmd_left, cmd_right, CMD_FUNC, (byte) 0, null, (byte) 0, (byte) 1);
        }
    }

    public static Command generateCommandBySource(byte[] data, byte num, byte sum, byte... cmdID) {
        return new Command(cmdID[0], cmdID[1], CMD_FUNC, (byte) data.length, data, num, sum);
    }
}
