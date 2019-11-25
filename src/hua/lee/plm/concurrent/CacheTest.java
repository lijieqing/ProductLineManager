package hua.lee.plm.concurrent;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 构建高效且可伸缩的结果缓存
 *
 * @author lijie
 * @create 2019-11-19 15:23
 **/
public class CacheTest {
}

interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}

class ExpensiveFunction implements Computable<String, BigInteger> {

    @Override
    public BigInteger compute(String arg) {
        return new BigInteger(arg);
    }
}

/**
 * 线程安全，但是 compute 方法同时只能由一个线程访问
 *
 * @param <A>
 * @param <V>
 */
class Memorizer1<A, V> implements Computable<A, V> {
    private final Map<A, V> cache = new HashMap<>();
    private final Computable<A, V> c;

    public Memorizer1(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public synchronized V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}

/**
 * 可以多线程同步访问，但是当并发重复计算时，会造成较大开销
 * 例如我们正在计算一个开销很大的运算f(27),另一个线程也准备查询 f(27)，当前策略会造成重复计算。
 *
 * @param <A>
 * @param <V>
 */
class Memorizer2<A, V> implements Computable<A, V> {
    private final Map<A, V> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memorizer2(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}

/**
 * Memorizer3 有比较好的并发性（基本上是源于 ConcurrentHashMap 高效的并发性），它只有一个缺陷，即两个线程计算出相同值的漏洞。
 * 这个漏洞的发生概率要远小于 Memorizer2 发生的概率，
 * 但由于 compute 中的 if 代码块是非原子的，因此两个线程仍有可能在同一时间内调用 compute 来计算相同的值
 * 即二者都没有在缓存中找到期望的值，因此都开始计算。
 *
 * @param <A>
 * @param <V>
 */
class Memorizer3<A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memorizer3(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if (f == null) {
            Callable<V> eval = new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return c.compute(arg);
                }
            };
            FutureTask<V> ft = new FutureTask<>(eval);
            f = ft;
            cache.put(arg, f);
            ft.run();
        }
        try {
            return f.get();
        } catch (ExecutionException e) {
            throw new InterruptedException(e.getCause().getMessage());
        }
    }
}

class Memorizer<A, V> implements Computable<A, V> {
    private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();

    public Memorizer(Computable<A, V> c) {
        this.c = c;
    }

    private final Computable<A, V> c;

    @Override
    public V compute(A arg) throws InterruptedException {
        while (true){
            Future<V> f = cache.get(arg);
            if (f == null){
                Callable<V> eval = new Callable<V>() {
                    @Override
                    public V call() throws Exception {
                        return c.compute(arg);
                    }
                };
                FutureTask<V> ft = new FutureTask<>(eval);
                f = cache.putIfAbsent(arg,ft);
                if (f == null){
                    f = ft;
                    ft.run();
                }
            }
            try {
                return f.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }
}
