package hua.lee.plm.engine;


import hua.lee.plm.bean.SenderCommand;
import hua.lee.plm.type.CommandType;
import hua.lee.plm.type.ParamType;
import hua.lee.plm.type.ResultType;
import hua.lee.plm.vo.CommandListVO;
import hua.lee.plm.vo.CommandVO;
import lee.hua.xmlparse.api.XMLAPI;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * test
 *
 * @author lijie
 * @create 2018-10-30 15:30
 **/
public class Test {


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        XMLAPI.setXmlBeanScanPackage("hua.lee.plm");

        CommandListVO list = (CommandListVO) XMLAPI.readXML(new FileInputStream("/Users/lijie/Desktop/COM.xml"));
        CommandManager manager = new CommandManager();
        SenderCommand.Builder builder = new SenderCommand.Builder();
        List<SenderCommand> scList = new ArrayList<>();
        for (CommandVO commandVO : list.getCommandList()) {
            SenderCommand cmd = builder.mCommandID(commandVO.getCmd_ID())
                    .mCommandDesc("af pic")
                    .mCommandParam("91")
                    .mCommandType(CommandType.Send)
                    .mParamType(ParamType.String)
                    .mResultType(ResultType.HEX)
                    .build();
            scList.add(cmd);
        }
        manager.initServer();
        for (SenderCommand senderCommand : scList) {
            manager.addCmdToSendPool(senderCommand);
            if (senderCommand.getCommandID().equals("14d5")){
                Thread.sleep(1000*15);
            }else {
                Thread.sleep(1000*2);
            }
        }

        try {
            Thread.sleep(1000 * 6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        manager.closeServer();
        System.out.println("close port");
    }
}
