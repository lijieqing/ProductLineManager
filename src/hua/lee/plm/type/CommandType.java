package hua.lee.plm.type;

public enum CommandType {
    /**
     * 发送指令、系统休眠指令
     */
    Send(0, "send cmd type"), Sleep(1, "sleep cmd type"),
    ACK(2,"ACK"),NACK(3,"NACK");
    private int value;
    private String desc;

    CommandType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}