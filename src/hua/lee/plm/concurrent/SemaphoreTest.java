package hua.lee.plm.concurrent;

import java.util.concurrent.Semaphore;

/**
 * Semaphore Test
 *
 * @author lijie
 * @create 2019-11-12 13:44
 **/
public class SemaphoreTest {
    public static void main(String[] args) {
        SemaphoreService semaphoreService = new SemaphoreService();
        for (int i = 0; i < 5; i++) {
            new Thread(){
                @Override
                public void run() {
                    try {
                        semaphoreService.working();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}

class SemaphoreService {
    //初始化信号量，并设置 permit 为1。permit是多少，表示同一个时刻，只允许多少个线程同时运行指定代码
    private Semaphore semaphore = new Semaphore(3);

    public void working() throws InterruptedException {
        semaphore.acquire();
        System.out.println(Thread.currentThread().getName() + " : start");
        Thread.sleep(2000);
        System.out.println(Thread.currentThread().getName() + " : end");
        semaphore.release();
    }
}
