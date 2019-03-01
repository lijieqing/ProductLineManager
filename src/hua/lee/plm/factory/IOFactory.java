package hua.lee.plm.factory;

import hua.lee.plm.base.CommunicatePort;
import hua.lee.plm.bean.CP2102CommunicatePort;

/**
 * 通讯 IO 工厂
 *
 * @author lijie
 * @create 2019-02-22 11:42
 **/
public final class IOFactory {
    private static CommunicatePort port;

    public synchronized static CommunicatePort initPort() {
        if (port == null) {
            port = new CP2102CommunicatePort();
        }
        return port;
    }

    public static void resetPort() {
        port.closePort();
    }
}
