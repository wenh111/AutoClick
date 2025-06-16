package com.wenh.autoclick.thread;

public abstract class Run implements Runnable {
    private Thread mThread;

    public Run() {
    }

    public boolean interrupted() {
        return this.mThread == null || this.mThread.isInterrupted();
    }

    public void stop() {
        if (this.mThread != null && !this.mThread.isInterrupted()) {
            this.mThread.interrupt();
        }

    }

    public void run() {
        this.mThread = Thread.currentThread();
    }
}