package hua.lee.plm.base;

/**
 * 数据接收回调
 *
 * @author lijie
 * @create 2019-01-08 17:06
 **/
public interface RxDataCallback {
    void notifyDataReceived(String cmdID,byte[] data);
}
