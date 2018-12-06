package hua.lee.plm.engine;

import hua.lee.plm.base.Command;
import hua.lee.plm.vo.CommandListVO;
import hua.lee.plm.vo.CommandVO;
import lee.hua.xmlparse.api.XMLAPI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令工厂
 *
 * @author lijie
 * @create 2018-12-05 22:43
 **/
public final class CommandFactory {
    private CommandFactory(){}
    private static Map<String, CommandVO> cmdMap = new HashMap<>();

    public static final byte DATA_TYPE = 0x00;
    public static final byte ACK_TYPE = 0x10;

    /**
     * @param filePath "/Users/lijie/Desktop/COM.xml"
     * @return
     * @throws FileNotFoundException
     */
    public static CommandListVO readConfig(String filePath) throws IOException, ClassNotFoundException {
        XMLAPI.setXmlBeanScanPackage("hua.lee.plm");
        CommandListVO res = (CommandListVO) XMLAPI.readXML(new FileInputStream(filePath));
        for (CommandVO commandVO : res.getCommandList()) {
            cmdMap.put(commandVO.getCmd_ID(), commandVO);
        }
        return res;
    }

    public static CommandVO getCommadVO(String cmdID) {
        return cmdMap.get(cmdID);
    }
    public static byte[] generateACKCMD(byte... cmdID) {
        byte[] ack = new byte[9];
        ack[0] = (byte) Command.FRAME_HEAD;
        ack[1] = ACK_TYPE;
        ack[2] = cmdID[0];
        ack[3] = cmdID[1];
        ack[4] = 0x00;
        ack[5] = 0x00;
        ack[6] = 0x01;
        ack[7] = (byte) calCRC(ack);
        ack[8] = (byte) Command.FRAME_TAIL;
        return ack;
    }

    /**
     * 计算 CRC 校验值
     * @param originFrame 数据帧
     * @return CRC value
     */
    public static int calCRC(byte[] originFrame) {
        int sum = 0;
        for (int i = 0; i < originFrame.length; i++) {
            if (i > 0 && i < originFrame.length - 2) {
                sum += (originFrame[i] & 0xff);
            }
        }
        if (sum > 0x100) {
            sum = sum % 0x100;
        }
        return 0x100 - sum;
    }
}
