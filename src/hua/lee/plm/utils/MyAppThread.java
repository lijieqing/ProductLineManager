package hua.lee.plm.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class MyAppThread extends Thread {
    public static final String DEFAULT_NAME = "FenAppThread";
    private static volatile boolean debugLifecycle = true;
    private static final AtomicInteger created = new AtomicInteger();
    private static final AtomicInteger alive = new AtomicInteger();


    public MyAppThread(Runnable r) {
        this(r, DEFAULT_NAME);
    }

    public MyAppThread(Runnable r, String poolName) {
        super(r, poolName + "-" + created.incrementAndGet());
        setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("uncaught in thread " + t.getName() + "\n" + e.getMessage());
            }
        });
    }

    @Override
    public void run() {
        boolean debug = debugLifecycle;
        if (debug) {
            System.out.println("Created " + getName());
        }
        try {
            alive.incrementAndGet();
            super.run();
        } finally {
            alive.decrementAndGet();
            if (debug) {
                System.out.println("Exiting " + getName());
            }
        }
    }

    public static int getThreadCreated() {
        return created.get();
    }

    public static int getThreadAlive() {
        return alive.get();
    }

    public static boolean getDebug() {
        return debugLifecycle;
    }

    public static void setDebug(boolean b) {
        debugLifecycle = b;
    }
}
