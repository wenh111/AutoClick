package com.wenh.autoclick.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CalcThreadPool {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE;
    private static final int MAXIMUM_POOL_SIZE;
    private static final int KEEP_ALIVE = 1;
    private static final ThreadFactory sThreadFactory;
    private static final BlockingQueue<Runnable> sPoolWorkQueue;
    public static ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    public CalcThreadPool() {
    }

    public static synchronized void execute(Runnable run) {
        if (run != null) {
            if (THREAD_POOL_EXECUTOR == null || THREAD_POOL_EXECUTOR.isShutdown()) {
                THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 1L, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
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

    static {
        CORE_POOL_SIZE = CPU_COUNT + 1;
        MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
        sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "CalcThread #" + this.mCount.getAndIncrement());
            }
        };
        sPoolWorkQueue = new LinkedBlockingQueue(128);
    }
}