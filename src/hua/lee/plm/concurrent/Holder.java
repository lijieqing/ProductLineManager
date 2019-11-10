package hua.lee.plm.concurrent;

/**
 * Holder class
 *
 * @author lijie
 * @create 2019-10-25 13:51
 **/
public class Holder {
    private int n;

    public Holder(int n) {
        this.n = n;
    }

    public void assertSanity(){
        if (n!=n){
            throw new AssertionError("this statement is false");
        }
    }
}
