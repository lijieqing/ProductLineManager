package hua.lee.plm.type;

public enum CommandType {
    /**
     * 发送指令、系统休眠指令
     */
    Send(1, "send cmd type"), Sleep(2, "sleep cmd type");
    private int value;
    private String desc;

    CommandType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}