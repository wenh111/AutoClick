package com.gosnin.autoclick.thread;


import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadObserver implements LifecycleObserver {
    private static ConcurrentHashMap<LifecycleOwner, List<Runnable>> mTasks = new ConcurrentHashMap();

    public ThreadObserver() {
    }

    public synchronized void subscribe(LifecycleOwner lifecycleOwner, Run runnable) {
        lifecycleOwner.getLifecycle().addObserver(this);
        List runnableList;
        if (!mTasks.containsKey(lifecycleOwner)) {
            runnableList = Collections.synchronizedList(new ArrayList());
            runnableList.add(runnable);
            mTasks.put(lifecycleOwner, runnableList);
        } else {
            runnableList = (List) mTasks.get(lifecycleOwner);
            runnableList.add(runnable);
        }

    }

    public synchronized void unSubscribe(LifecycleOwner lifecycleOwner, Run runnable) {
        if (mTasks.containsKey(lifecycleOwner)) {
            List<Runnable> runnableList = (List) mTasks.get(lifecycleOwner);
            runnableList.remove(runnable);
        }

    }

    public synchronized void onDestroy(LifecycleOwner source) {
        List<Runnable> runnableList = (List) mTasks.get(source);
        Iterator var3 = runnableList.iterator();

        while (var3.hasNext()) {
            Runnable runnable = (Runnable) var3.next();
            Threads.remove(runnable);
        }

        mTasks.remove(source);
    }
}