package hua.lee.plm.base;

/**
 * 通讯接口
 *
 * @author lijie
 * @create 2018-10-30 17:48
 **/
public interface ICommunicate {
    /**
     * 初始化串口
     */
    void initPort();
    /**
     * 发送串口命令
     */
    void send(Command cmd);
    /**
     * 关闭串口
     */
    void closePort();
}
