package hua.lee.plm.concurrent;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * FutureTask Test
 *
 * @author lijie
 * @create 2019-11-10 10:47
 **/
public class FutureTaskTest {
    @Test
    public void primaryTest() throws ExecutionException, InterruptedException {
        DataGet dataGet = new DataGet();
        Callable<String> userInfoCall = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return dataGet.getUserInfo("Hello");
            }
        };
        Callable<String> userAddressCall = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return dataGet.getUserAddress("Hello");
            }
        };

        FutureTask<String> userInfoTask = new FutureTask<>(userInfoCall);
        FutureTask<String> userAddressTask = new FutureTask<>(userAddressCall);

        new Thread(userInfoTask).start();
        new Thread(userAddressTask).start();

        String userInfo = userInfoTask.get();
        System.out.println("userInfo: " + userInfo);

        String userAddress = userAddressTask.get();
        System.out.println("userAddress: " + userAddress);
    }

    @Test
    public void blockingTest() throws ExecutionException, InterruptedException {
        PreLoader preLoader = new PreLoader();

        System.out.println("current is " + System.currentTimeMillis());
        preLoader.start();
        String info = preLoader.get();

        System.out.println(info);
    }

}

class PreLoader {
    private final FutureTask<String> future = new FutureTask<>(new Callable<String>() {
        @Override
        public String call() throws Exception {
            DataGet dataGet = new DataGet();
            return dataGet.getUserAddress("H");
        }
    });

    private final Thread thread = new Thread(future);

    void start() {
        thread.start();
    }

    String get() throws ExecutionException, InterruptedException {
        return future.get();
    }
}

class DataGet {
    String getUserInfo(String name) {
        String userInfo;
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userInfo = "userInfo is " + System.currentTimeMillis();
        return userInfo;
    }

    String getUserAddress(String name) {
        String address;
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        address = "address is " + System.currentTimeMillis();
        return address;
    }
}
