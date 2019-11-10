package hua.lee.plm.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * Latch test
 *
 * @author lijie
 * @create 2019-11-08 14:47
 **/
public class CountDownLatchTest {
    private static final int TASK_SIZE = 10;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch endSignal = new CountDownLatch(TASK_SIZE);
        for (int i = 0; i < TASK_SIZE; ++i) {
            new Thread(new Worker(startSignal,endSignal)).start();
        }
        System.out.println("线程初始化，start调用完成，准备休眠1s");
        Thread.sleep(1000);
        startSignal.countDown();
        System.out.println("开始信号已发起，等待任务自行完成");
        endSignal.await();

        System.out.println("任务执行完成");
    }

}

class Worker implements Runnable {
    private final CountDownLatch startSignal;
    private final CountDownLatch endSignal;

    public Worker(CountDownLatch startSignal, CountDownLatch endSignal) {
        this.startSignal = startSignal;
        this.endSignal = endSignal;
    }

    @Override
    public void run() {
        try {
            startSignal.await();
            System.out.println("Thread name : " + Thread.currentThread().getName());
            Thread.sleep(1500);
            System.out.println(" Done Thread id : " + Thread.currentThread().getId());
            endSignal.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
