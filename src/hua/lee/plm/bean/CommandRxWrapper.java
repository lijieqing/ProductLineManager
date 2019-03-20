package hua.lee.plm.bean;

import com.sun.istack.internal.NotNull;
import hua.lee.plm.base.CommandWrapper;
import hua.lee.plm.base.GlobalCommandReceiveListener;
import hua.lee.plm.base.PLMContext;
import hua.lee.plm.base.RxDataCallback;
import hua.lee.plm.vo.CommandVO;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Command Wrapper
 *
 * @author lijie
 * @create 2019-01-08 14:50
 **/
public class CommandRxWrapper extends CommandWrapper {
    private static Map<String, List<RxDataCallback>> listenerMap = new HashMap<>();
    private static GlobalCommandReceiveListener globalReceiveListener = null;
    private byte[] data;
    private boolean receiving = true;
    private List<CP210xCommand> cmdList;
    private String cmdID;


    public CommandRxWrapper() {
        cmdList = new LinkedList<>();
    }

    /**
     * 注册指定 cmd id 的数据接收器
     *
     * @param cmdID    命令 ID
     * @param callback 回调
     */
    public static void addRxDataCallBack(@NotNull String cmdID, @NotNull RxDataCallback callback) {
        List<RxDataCallback> callbackList = listenerMap.get(cmdID);
        if (callbackList != null) {
            callbackList.add(callback);
        } else {
            callbackList = new LinkedList<>();
            callbackList.add(callback);
            listenerMap.put(cmdID, callbackList);
        }
    }

    /**
     * 移除指定 cmd ID 的监听
     *
     * @param cmdID    cmd ID
     * @param callback 回调
     */
    public static void removeRxDataCallBack(@NotNull String cmdID, @NotNull RxDataCallback callback) {
        List<RxDataCallback> callbackList = listenerMap.get(cmdID);
        if (callbackList != null) {
            callbackList.remove(callback);
        }
    }

    /**
     * 注册全局数据接收，对于所有的数据传输都会调用此接口
     *
     * @param gls 全局监听器
     */
    public static void addGlobalRXListener(@NotNull GlobalCommandReceiveListener gls) {
        globalReceiveListener = gls;
    }

    public boolean isReceiving() {
        return receiving;
    }

    public void startReceiving() {
        receiving = true;
    }

    public void received() {
        receiving = false;
        onRxDataRec();
    }

    public String getCmdID() {
        return cmdID;
    }

    public void setCmdID(String cmdID) {
        this.cmdID = cmdID;
    }


    public void addCommand(CP210xCommand cmd) {
        cmdList.add(cmd);
    }

    private void onRxDataRec() {
        loadCommandData();
        List<RxDataCallback> callList = listenerMap.get(cmdID);
        if (callList != null) {
            for (RxDataCallback callback : listenerMap.get(cmdID)) {
                callback.notifyDataReceived(cmdID, data);
            }

        }
        if (globalReceiveListener != null) {
            globalReceiveListener.onRXWrapperReceived(cmdID, data);
        }
        cmdList.clear();
    }

    private void loadCommandData() {
        int len = 0;
        Map<Integer, CP210xCommand> map = new HashMap<>();
        for (CP210xCommand command : cmdList) {
            map.put((int) command.getCmdNum(), command);
        }
        //判断接受帧数与数据帧数是否一致
        int recvSize = map.keySet().size();
        int targetSum = cmdList.get(0).getCmdSum();
        if (recvSize != targetSum) {
            PLMContext.d("UsbCommTask", recvSize + " <= received |||| target => " + targetSum);
        } else {
            for (Integer integer : map.keySet()) {
                PLMContext.d("UsbCommTask", integer + " <= key |||| target => " + map.get(integer));
                len += map.get(integer).getDataLen();
            }
            data = new byte[len];
            byte[] temp;
            int curPos = 0;

            for (int i = 0; i < cmdList.size(); i++) {
                CP210xCommand cmd = map.get(i);
                if (cmd != null) {
                    temp = cmd.getData();
                    if (temp != null) {
                        System.arraycopy(temp, 0, data, curPos, temp.length);
                        curPos += temp.length;
                    }
                }
            }
        }
    }

    public void clearCommands() {
        if (cmdList != null) {
            cmdList.clear();
        }
    }
}
