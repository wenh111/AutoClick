package com.wenh.autoclick.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;


public class Threads {
    private static final HandlerThread mHandlerThread = new HandlerThread("Thread BackGround");
    private static final Handler mBackHandler;
    private static final Handler mUIHandler;

    public Threads() {
    }

    public static void ui(Runnable runnable) {
        mUIHandler.post(runnable);
    }

    public static void ui(Runnable runnable, int delayMillis) {
        if (delayMillis > 0) {
            mUIHandler.postDelayed(runnable, (long) delayMillis);
        } else {
            mUIHandler.post(runnable);
        }

    }

    public static void back(Runnable runnable) {
        mBackHandler.post(runnable);
    }

    public static void back(Runnable runnable, int delayMillis) {
        if (delayMillis > 0) {
            mBackHandler.postDelayed(runnable, (long) delayMillis);
        } else {
            mBackHandler.post(runnable);
        }

    }

    public static void io(Runnable runnable) {
        IOThreadPool.execute(runnable);
    }

    public static void calc(Runnable runnable) {
        CalcThreadPool.execute(runnable);
    }

    public static void sleep(long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException var3) {
            Thread.currentThread().interrupt();
            var3.printStackTrace();
        }

    }

    public static void remove(Runnable runnable) {
        mUIHandler.removeCallbacks(runnable);
        mBackHandler.removeCallbacks(runnable);
        IOThreadPool.cancel(runnable);
        CalcThreadPool.cancel(runnable);
        if (runnable instanceof Run) {
            ((Run) runnable).stop();
        }

    }

    static {
        mHandlerThread.start();
        mBackHandler = new Handler(mHandlerThread.getLooper());
        mUIHandler = new Handler(Looper.getMainLooper());
    }
}
