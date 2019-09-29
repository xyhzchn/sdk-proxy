package com.cn.ceshi.util;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class LooperThread implements Runnable {
    private static LinkedBlockingQueue<HashMap> queque = new LinkedBlockingQueue<>();

    public volatile Thread thread;

    public LooperThread() {
        this(null);
    }

    public LooperThread(String name) {
        thread = new Thread(this, name == null ? getClass().getSimpleName() : name);
    }

    public void start() {
        try {
            if (thread != null && !thread.isAlive()) {
                thread.start();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            if (thread != null) {
                thread.interrupt();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            thread = null;
        }
    }

    public void run() {
        try {
            while(!thread.isInterrupted()) {
                loop();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public abstract void loop() throws Throwable;
}
