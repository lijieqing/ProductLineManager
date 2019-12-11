package hua.lee.plm.concurrent;

import org.junit.Test;
import sun.misc.Request;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * Executor 测试
 *
 * @author lijie
 * @create 2019-12-04 11:13
 **/
public class ExecutorTest {
    @Test
    public void singleThread() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8088);
        while (true) {
            Socket conn = serverSocket.accept();
            handleRequest(conn);
        }
    }

    @Test
    public void perThreadTask() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8088);
        while (true) {
            Socket conn = serverSocket.accept();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    handleRequest(conn);
                }
            };
            new Thread(r).start();
        }
    }

    @Test
    public void limitExecutorTask() throws IOException {
        final int nThreads = 100;
        ExecutorService exec = Executors.newFixedThreadPool(nThreads);
        ServerSocket serverSocket = new ServerSocket(8088);
        while (true) {
            Socket conn = serverSocket.accept();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    handleRequest(conn);
                }
            };
            exec.execute(r);
        }
    }

    @Test
    public void testTimer() {
        Timer timer = new Timer();
        System.out.println("Timer Test Start " + new Date());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("001 working current " + new Date());
                try {
                    Thread.sleep(4 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("001 working current " + new Date());
            }
        }, 1000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("002 working current " + new Date());

                    Thread.sleep(1000);
                    System.out.println("002 working current " + new Date());

                    Thread.sleep(1000);
                    System.out.println("002 working current " + new Date());

                    Thread.sleep(1000);
                    System.out.println("002 working current " + new Date());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 2000);

    }
    @Test
    public void testScheduled(){
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        System.out.println("scheduled test " + new Date());
        ScheduledFuture<?> work1 = executor.schedule(new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("001 Worker " + new Date());
                return "work1 finish";
            }
        }, 1, TimeUnit.SECONDS);
        ScheduledFuture<?> work2 = executor.schedule(new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    Thread.sleep(1000);
                    System.out.println("002 Worker " + new Date());
                    Thread.sleep(1000);
                    System.out.println("002 Worker " + new Date());
                    Thread.sleep(1000);
                    System.out.println("002 Worker " + new Date());
                    Thread.sleep(1000);
                    System.out.println("002 Worker " + new Date());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "work2 Finish";
            }
        }, 2, TimeUnit.SECONDS);

        try {
            System.out.println(work1.get());
            System.out.println(work2.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }

    private void handleRequest(Socket conn) {

    }

    class LifecycleWebServer {
        private ExecutorService exec;

        public void start() throws IOException {
            ServerSocket socket = new ServerSocket(80);
            while (!exec.isShutdown()) {
                try {
                    Socket conn = socket.accept();
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            handleRequest(conn);
                        }

                    });
                } catch (RejectedExecutionException e) {
                    if (!exec.isShutdown()) {
                        System.out.println("task submission reject::" + e);
                    }
                }

            }
        }

        public void stop() {
            exec.shutdown();
        }

        void handleRequest(Socket conn) {
            Request req = readRequest(conn);
            if (isShutdownRequest(req)) {
                stop();
            } else {
                dispatchRequest(req);
            }
        }

        private void dispatchRequest(Request req) {

        }

        private boolean isShutdownRequest(Request req) {
            return false;
        }

        private Request readRequest(Socket conn) {
            return null;
        }
    }
}
