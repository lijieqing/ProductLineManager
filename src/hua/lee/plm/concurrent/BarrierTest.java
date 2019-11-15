package hua.lee.plm.concurrent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

/**
 * CyclicBarrier、Exchanger
 *
 * @author lijie
 * @create 2019-11-14 14:53
 **/
public class BarrierTest {

    @Test
    public void cyclicBarrierTest() throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println("Barrier Action notify");
            }
        });

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CyclicBarrierTask t = new CyclicBarrierTask(cyclicBarrier);
            threads.add(t);
            t.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("结束任务");

    }

    @Test
    public void exchangerTest() throws InterruptedException {
        Exchanger<Integer> exchanger = new Exchanger<>();
        List<Thread> threads = new ArrayList<>();
        UserA a = new UserA(exchanger);
        UserB b = new UserB(exchanger);
        threads.add(a);
        threads.add(b);
        a.start();
        b.start();

        for (Thread thread : threads) {
            thread.join();
        }
    }
}

class UserA extends Thread {
    private Exchanger<Integer> exchanger;

    public UserA(Exchanger<Integer> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                TimeUnit.SECONDS.sleep(2);
                int data = i;
                System.out.println("User A before exchange : " + data);
                data = exchanger.exchange(data);
                System.out.println("User A after exchange : " + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class UserB extends Thread {
    private Exchanger<Integer> exchanger;
    private static int data = 999;

    public UserB(Exchanger<Integer> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {

        for (int i = 0; i < 5; i++) {
            data = 999 + i;
            try {
                System.out.println("User B before exchange : " + data);
                TimeUnit.SECONDS.sleep(4);
                data = exchanger.exchange(data);
                System.out.println("User B after exchange : " + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class CyclicBarrierTask extends Thread {
    private CyclicBarrier cb;

    public CyclicBarrierTask(CyclicBarrier cb) {
        this.cb = cb;
    }

    @Override
    public void run() {
        super.run();
        try {
            Thread.sleep(1500);
            System.out.println(getName() + "到达屏障 A");
            cb.await();
            System.out.println(getName() + "冲破屏障 A");
            Thread.sleep(2000);
            System.out.println(getName() + "到达屏障 B");
            cb.await();
            System.out.println(getName() + "冲破屏障 B");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

