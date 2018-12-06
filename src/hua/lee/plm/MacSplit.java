package hua.lee.plm;

/**
 * mac 地址分割
 *
 * @author lijie
 * @create 2018-12-06 10:01
 **/
public class MacSplit {
    public static void macSplit(){
        int start = 0xE848;
        int end = 0xffff;
        System.out.println("3036::: mac ==》"+Integer.toHexString(start+3035).toUpperCase());

        System.out.println(""+Integer.toHexString(0xF424+3035).toUpperCase());
    }

    public static void main(String[] args) {
        System.out.println((byte)0xca);
        macSplit();
    }
}
