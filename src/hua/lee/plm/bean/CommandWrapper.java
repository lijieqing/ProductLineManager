package hua.lee.plm.bean;

import com.sun.istack.internal.NotNull;
import hua.lee.plm.base.RxDataCallback;
import hua.lee.plm.vo.CommandVO;

import java.util.*;

/**
 * Command Wrapper
 *
 * @author lijie
 * @create 2019-01-08 14:50
 **/
public class CommandWrapper {
    private String cmdID = "";
    private LinkedList<Command> cmdList;
    private CommandVO cmdVO;
    private byte[] data;

    private static Map<String, List<RxDataCallback>> listenerMap = new HashMap<>();


    public CommandWrapper() {
        cmdList = new LinkedList<>();
    }

    public String getCmdID() {
        return cmdID;
    }

    public void setCmdID(String cmdID) {
        this.cmdID = cmdID;
    }

    public CommandVO getCmdVO() {
        return cmdVO;
    }

    public void setCmdVO(CommandVO cmdVO) {
        this.cmdVO = cmdVO;
    }

    public void addCommand(Command cmd) {
        cmdList.add(cmd);
    }

    public static void addRxDataCallBack(@NotNull String cmdID, @NotNull RxDataCallback callback) {
        List<RxDataCallback> callbackList = listenerMap.computeIfAbsent(cmdID, k -> new LinkedList<>());
        callbackList.add(callback);
    }

    public static void removeRxDataCallBack(@NotNull String cmdID, @NotNull RxDataCallback callback) {
        List<RxDataCallback> callbackList = listenerMap.get(cmdID);
        if (callbackList != null) {
            callbackList.remove(callback);
        }
    }

    public void onRxDataRec() {
        loadCommandData();

        for (RxDataCallback callback : listenerMap.get(cmdID)) {
            callback.notifyDataReceived(cmdID, data);
        }

        cmdList.clear();
    }

    private void loadCommandData() {
        int len = 0;
        Map<Integer, Command> map = new HashMap<>();
        for (Command command : cmdList) {
            len += command.getDataLen();
            map.put((int) command.getCmdNum(), command);
        }

        data = new byte[len];
        byte[] temp;
        int curPos = 0;

        for (int i = 0; i < cmdList.size(); i++) {
            temp = map.get(i).getData();
            System.arraycopy(temp, 0, data, curPos, temp.length);
            curPos += temp.length;
        }
    }
}
