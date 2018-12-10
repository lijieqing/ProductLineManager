package hua.lee.plm.engine;

import com.sun.istack.internal.NotNull;
import hua.lee.plm.base.DataReceivedCallback;
import hua.lee.plm.base.ICommandWorker;
import hua.lee.plm.base.ICommunicate;
import hua.lee.plm.bean.RequestCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 命令管理类
 *
 * @author lijie
 * @create 2018-10-30 16:56
 **/
public class CommandManager {
    /**
     * 发送区域命令集合
     */
    private List<RequestCommand> cmdList = new ArrayList<>();
    /**
     * 通讯异常命令集合
     */
    private List<RequestCommand> errcmdList = new ArrayList<>();
    /**
     * 通讯引擎类
     */
    private ICommunicate comm = new CommunicateEngine();
    /**
     * 守护线程，当发送集合不为空时执行发送命令
     */
    private Timer watchCat= null;
    private static volatile boolean running = false;

    /**
     * 串行发送命令
     */
    private void serialRun() {
        boolean pass = true;
        running = true;
        while (cmdList.size() > 0 && pass) {
            int pos = cmdList.size() - 1;
            ICommandWorker worker = cmdList.get(pos);
            pass = worker.worker(comm);

            cmdList.remove(pos);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        running = false;
    }

    /**
     * 发送消息到消息池
     *
     * @param cmd
     */
    public void addCmdToSendPool(RequestCommand cmd) {
        cmdList.add(cmd);
    }

    /**
     * 初始化命令发送服务
     */
    public void initServer() {
        if (watchCat !=null){
            System.out.println("已初始化 watch cat，无需重复！！");
            return;
        }else {
            watchCat = new Timer();
            watchCat.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (cmdList.size() > 0 && !running) {
                        serialRun();
                    }
                }
            }, 200, 200);
        }
    }

    public void addDataReceivedListener(@NotNull DataReceivedCallback callback){
        comm.addReceivedCallback(callback);
    }

    public boolean isWatching() {
        return watchCat!=null;
    }

    public void closeServer() {
        if (watchCat != null) {
            watchCat.cancel();
            watchCat = null;
        }
        if (comm != null) {
            comm.closePort();
            comm = null;
        }
    }
}
