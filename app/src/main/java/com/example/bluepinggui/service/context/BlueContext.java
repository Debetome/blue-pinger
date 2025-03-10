package com.example.bluepinggui.service.context;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.bluepinggui.service.strategy.BlueService;

public class BlueContext {
    public static final String TAG = "BlueContext";
    private volatile boolean isRunning = false;

    private final BlueService blueService;
    private final String contextName;
    private final int threadCount;
    private final int minAttempts;
    private final int maxAttempts;

    public BlueContext(BlueService blueService, String contextName, int threadCount, int minAttempts, int maxAttempts) {
        this.blueService = blueService;
        this.contextName = contextName;
        this.threadCount = threadCount;
        this.minAttempts = minAttempts;
        this.maxAttempts = maxAttempts;
    }

    public BlueContext(BlueContextBuilder builder) {
        this.blueService = builder.getService();
        this.contextName = builder.getContextName();
        this.threadCount = builder.getThreadCount();
        this.minAttempts = builder.getMinAttempts();
        this.maxAttempts = builder.getMaxAttempts();
    }

    private void executeThreadLogic(Context context, String bluetoothAddress, Handler mainHandler) {
        int attempts = (int) (Math.random() * (maxAttempts - minAttempts + 1)) + minAttempts;
        for (int a = 0; a < attempts; a++) {
            blueService.execute(context, bluetoothAddress, mainHandler);
        }
    }

    private void executeThreadLoop(Context context, String bluetoothAddress, Handler mainHandler) {
        String tag = String.format("%s.%s", TAG, contextName);
        while (isRunning) {
            try {
                long randomDelay = 50 + (long) (Math.random() * (300 - 50));
                executeThreadLogic(context, bluetoothAddress, mainHandler);
                Thread.sleep(randomDelay);
            } catch (InterruptedException e) {
                Log.e(tag, "Interrupted!", e);
                break;
            }
        }
    }

    public void start(Context context, String bluetoothAddress) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        isRunning = true;

        for (int t = 0; t < threadCount; t++) {
            executor.execute(() -> {
                executeThreadLoop(context, bluetoothAddress, mainHandler);
            });
        }
    }

    public void stop() {
        isRunning = false;
    }

    // Set BlueContext's setters
    public BlueService getService() {
        return this.blueService;
    }

    public String getContextName() {
        return this.contextName;
    }

    public int getThreadCount() {
        return this.threadCount;
    }

    public int getMinAttempts() {
        return this.minAttempts;
    }

    public int getMaxAttempts() {
        return this.maxAttempts;
    }
}
