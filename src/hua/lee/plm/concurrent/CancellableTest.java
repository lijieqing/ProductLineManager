package hua.lee.plm.concurrent;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * 任务取消操作
 *
 * @author lijie
 * @create 2020-01-26 19:24
 **/
public class CancellableTest {

    /**
     * 通过 boolean 轮询来取消任务
     */
    @Test
    public void aSecondOfPrimesBoolean() {
        PrimeGenerator generator = new PrimeGenerator();
        new Thread(generator).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            generator.cancel();
        }

        List<BigInteger> ls = generator.get();
        System.out.println(Arrays.toString(ls.toArray()));
    }

    /**
     * 通过触发中断异常来取消任务
     */
    @Test
    public void aSecondOfPrimesInterrupt() {
        BlockingQueue<BigInteger> queue = new ArrayBlockingQueue<>(1000 * 100);
        CountDownLatch latch = new CountDownLatch(1);
        PrimeProducer producer = new PrimeProducer(queue, latch);
        producer.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            producer.cancel();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("中断任务测试失败");
        }
        System.out.println("中断任务测试结束");

    }

    /**
     * 定时取消任务，利用 Future get 方法，设置延迟时间，
     * 通过 TimeoutException 来判断超时，然后进行任务取消操作
     */
    @Test
    public void timedRun() {
        ExecutorService exec = Executors.newFixedThreadPool(10);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("task was interrupted,so we return");
                    return;
                } finally {
                    System.out.println("finally doing here");
                }
                System.out.println("task runnable after 5s");
            }
        };

        Future<?> task = exec.submit(r);

        try {
            task.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("we get task interrupt exception");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("time out so we cancel task");
            task.cancel(true);
        }
    }

    @Test
    public void IOCancel() {
        CancellingExecutor executor = new CancellingExecutor(6, 12, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(12));

        executor.newTaskFor(new SocketUsingTask<Object>() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        });
    }
}

/**
 * 通过 boolean 标志位来取消任务
 */
class PrimeGenerator implements Runnable {
    private final List<BigInteger> primes = new ArrayList<>();
    private volatile boolean cancelled = false;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<>(primes);
    }
}

/**
 * 通过中断来取消任务
 */
class PrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;
    private CountDownLatch latch;

    public PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    public PrimeProducer(BlockingQueue<BigInteger> queue, CountDownLatch latch) {
        this.queue = queue;
        this.latch = latch;
    }

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("---producer doing work----");
            try {
                queue.put(p.nextProbablePrime());
            } catch (InterruptedException e) {
                System.out.println("queue put was interrupted,\n" + e.getMessage());
                //当出现中断异常时，在此处退出 while 循环
                break;
            }
        }

        latch.countDown();
    }

    public void cancel() {
        System.out.println("---cancel producer---");
        interrupt();
    }
}

/**
 * 针对任务中存在不可中断的阻塞方法（I/O 中的 read、write），
 * 可以利用一些异常（SocketException）来取消任务
 */
class ReadThread extends Thread {
    private final Socket socket;
    private final InputStream ins;

    public ReadThread(Socket socket) throws IOException {
        this.socket = socket;
        ins = socket.getInputStream();
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("this exception can be ignored");
        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {
        byte[] buf = new byte[1024];
        while (true) {
            try {
                int count = ins.read(buf);
                if (count < 0) {
                    System.out.println("read data failed");
                } else if (count > 0) {
                    System.out.println("we received data,process it now");
                }
            } catch (IOException e) {
                System.out.println("read Task was interrupt, info=" + e.toString());
                break;
            }

        }

    }
}

interface CancellableTask<T> extends Callable<T> {
    void cancel();

    RunnableFuture<T> newTask();
}

class CancellingExecutor extends ThreadPoolExecutor {
    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof CancellableTask) {
            return ((CancellableTask<T>) callable).newTask();
        }
        return super.newTaskFor(callable);
    }
}

abstract class SocketUsingTask<T> implements CancellableTask<T> {
    private Socket socket;

    protected synchronized void setSocket(Socket s) {
        socket = s;
    }

    @Override
    public synchronized void cancel() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("socket close failed");
        }
    }

    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                SocketUsingTask.this.cancel();
                return super.cancel(mayInterruptIfRunning);
            }
        };
    }
}


