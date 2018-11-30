package hua.lee.plm.engine;


import hua.lee.plm.bean.SenderCommand;

/**
 * test
 *
 * @author lijie
 * @create 2018-10-30 15:30
 **/
public class Test {


    public static void main(String[] args) {
        CommandManager manager = new CommandManager();
        manager.initCommand();
        manager.serialRun();
    }
}
