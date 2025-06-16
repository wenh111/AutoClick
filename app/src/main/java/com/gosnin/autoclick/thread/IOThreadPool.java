package com.gosnin.autoclick.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IOThreadPool {
    private static final int CORE_POOL_SIZE = 0;
    private static final int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE;
    private static final int KEEP_ALIVE = 30;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "IOThread #" + this.mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new SynchronousQueue();
    public static ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    public IOThreadPool() {
    }

    public static synchronized void execute(Runnable run) {
        if (run != null) {
            if (THREAD_POOL_EXECUTOR == null || THREAD_POOL_EXECUTOR.isShutdown()) {
                THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30L, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
            }

            THREAD_POOL_EXECUTOR.execute(run);
        }
    }

    public static synchronized void cancel(Runnable run) {
        if (isThreadPoolAlive()) {
            THREAD_POOL_EXECUTOR.remove(run);
        }

    }

    public static synchronized boolean contains(Runnable run) {
        return isThreadPoolAlive() ? THREAD_POOL_EXECUTOR.getQueue().contains(run) : false;
    }

    public static synchronized void stop() {
        if (isThreadPoolAlive()) {
            THREAD_POOL_EXECUTOR.shutdownNow();
        }

    }

    public static synchronized void shutdown() {
        if (isThreadPoolAlive()) {
            THREAD_POOL_EXECUTOR.shutdown();
        }

    }

    private static boolean isThreadPoolAlive() {
        return THREAD_POOL_EXECUTOR != null && (!THREAD_POOL_EXECUTOR.isShutdown() || THREAD_POOL_EXECUTOR.isTerminating());
    }
}
