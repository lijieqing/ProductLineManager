package hua.lee.plm.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 字符串解析测试
 *
 * @author lijie
 * @create 2019-05-08 14:24
 **/
public class StrTest {
    private static ThreadLocal<String> local = new ThreadLocal<String>(){
        @Override
        protected String initialValue() {
            System.out.println("init value");
            return "initialValue";
        }
    };
    private final Set<String> stooges = new HashSet<>();

    public StrTest() {
        stooges.add("A");
        stooges.add("B");
        stooges.add("C");
    }
    public void showContent(){
        System.out.println(Arrays.toString(stooges.toArray()));
    }

    public static void main(String[] args) {
        StrTest st = new StrTest();
        st.showContent();
    }


    static class A extends Thread{
        @Override
        public void run() {
            super.run();
            local.set("A");
            B b = new B();
            b.start();
            try {
                b.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(local.get());
        }
    }

    static class B extends Thread{
        @Override
        public void run() {
            super.run();
            local.set("B");
            System.out.println(local.get());
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
