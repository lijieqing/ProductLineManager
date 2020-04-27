package hua.lee.plm.utils;

import java.util.concurrent.*;

public final class ThreadUtils {
    private ThreadUtils() {
    }

    private static final ExecutorService executor = Executors.newCachedThreadPool(new FengThreadFactory());

    public static void runTaskOnBack(Runnable r) {
        executor.execute(r);
    }

    public static Future<Boolean> runBoolTask(Callable<Boolean> callable) {
        return executor.submit(callable);
    }

    public static Future<String> runStringTask(Callable<String> callable) {
        return executor.submit(callable);
    }

    public static Future<Integer> runIntTask(Callable<Integer> callable) {
        return executor.submit(callable);
    }

    private static class FengThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            return new MyAppThread(r);
        }
    }
    
}
