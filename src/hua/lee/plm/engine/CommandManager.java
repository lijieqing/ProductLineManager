package hua.lee.plm.engine;

import hua.lee.plm.base.ICommandWorker;
import hua.lee.plm.base.ICommunicate;
import hua.lee.plm.bean.SenderCommand;

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
    private List<SenderCommand> cmdList = new ArrayList<>();
    /**
     * 通讯异常命令集合
     */
    private List<SenderCommand> errcmdList = new ArrayList<>();
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
        }
        running = false;
    }

    /**
     * 发送消息到消息池
     *
     * @param cmd
     */
    public void addCmdToSendPool(SenderCommand cmd) {
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
