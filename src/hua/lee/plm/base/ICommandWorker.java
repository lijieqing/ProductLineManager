package hua.lee.plm.base;

/**
 * 指令执行接口
 *
 * @author lijie
 * @create 2018-10-30 17:15
 **/
public interface ICommandWorker {
    boolean worker(ICommunicate comm);
}
