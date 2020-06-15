package hua.lee.plm.test;

public class ComputorTest {
    public static void main(String[] args) {
        System.out.println("与运算" + (5 & 1));
        System.out.println("异或运算" + (4 ^ 1));
        System.out.println("或运算" + (4 | 8));
        System.out.println(0x2+0x32+0x02+0x32+0x01+0xbd);
        int res = (0x02&0xff << 24) | (0x32&0xff << 16) | (0x01&0xff << 8)| (0xbd&0xff);
        System.out.println(Integer.toHexString(res));
    }

}

class Example {
    public static void addAndPrint() {
        double d = addTwoTypes(1,88.88);
    }
    public static double addTwoTypes(int i, double d) {
        return d + i;
    }
}