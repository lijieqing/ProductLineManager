package hua.lee.plm.concurrent;

/**
 * Holder Test
 *
 * @author lijie
 * @create 2019-10-25 13:54
 **/
public class HolderTest {
    public Holder holder;

    public void initialize() {
        holder = new Holder(42);
    }

    public static void main(String[] args) {
        int a = 274%5;
        System.out.println(a);
    }
}
