package hua.lee.plm.type;

public enum ParamType {
    /**
     * 每个参数的返回值都有指定类型 HEX、String、VOID
     */
    VOID(0, "null result"), HEX(1, "hex type"), String(2, "string type");
    private int value;
    private String desc;

    ParamType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}