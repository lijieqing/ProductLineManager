package hua.lee.plm.engine;

import hua.lee.plm.base.Command;
import hua.lee.plm.base.ICommunicate;

/**
 * 通讯引擎
 *
 * @author lijie
 * @create 2018-10-30 18:23
 **/
public class CommunicateEngine implements ICommunicate {
    @Override
    public void send(Command cmd) {
        System.out.println(" usb ttl send cmd start ");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
