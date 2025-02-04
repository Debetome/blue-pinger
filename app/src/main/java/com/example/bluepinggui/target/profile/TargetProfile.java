package com.example.bluepinggui.target.profile;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.Semaphore;
import java.util.HashMap;
import java.lang.Math;

import com.example.bluepinggui.service.strategy.BluePingerService;
import com.example.bluepinggui.service.strategy.BlueService;
import com.example.bluepinggui.service.factory.BlueServiceFactory;
import com.example.bluepinggui.service.factory.BlueServiceType;
import com.example.bluepinggui.service.context.BlueContext;
import com.example.bluepinggui.service.context.BlueContextBuilder;

public class TargetProfile {
    public static final String TAG = "TargetProfile";

    private final String bluetoothAddress;
    private final int threadCount;
    private final BlueServiceType[] services;

    private final HashMap<String, BlueContext> blueContexts = new HashMap<>();

    public TargetProfile(TargetProfileBuilder builder) {
        this.bluetoothAddress = builder.getBluetoothAddress();
        this.threadCount = builder.getThreadCount();
        this.services = builder.getServices();
    }

    private void createContexts() {
        for (BlueServiceType service : services) {
            int sNumber = services.length;
            double cThreadCount = threadCount % 2 == 0 ?
                    (double) threadCount / sNumber :
                    service == BlueServiceType.PING ?
                            Math.ceil((double) threadCount / sNumber) :
                            Math.floor((double) threadCount / sNumber);

            BlueService blueService = BlueServiceFactory.createService(service);
            BlueContext blueContext = new BlueContextBuilder()
                    .contextName(String.format("%s service", service))
                    .service(blueService)
                    .threadCount((int)cThreadCount)
                    .build();

            blueContexts.put(blueContext.getContextName(), blueContext);
        }
    }

    public void launch(Context appContext) {
        if (threadCount < services.length) {
            Log.e(TAG, "The thread count cannot be smaller than the number of services");
            throw new IllegalStateException("The thread count cannot be smaller than the number of services");
        }

        if (bluetoothAddress == null || services.length == 0) {
            Log.e(TAG, "The thread count cannot be smaller than the number of services");
            throw new IllegalStateException("Bluetooth address and services are required");
        }

        createContexts();

        Semaphore semaphore = new Semaphore(10);  // Shared between both operations

        synchronized (BluePingerService.class) {
            try {
                semaphore.acquire();
                for (BlueContext blueContext : blueContexts.values()) {
                    blueContext.start(appContext, bluetoothAddress);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                Log.e(TAG, "Thread interrupted while acquiring semaphore", e);
            } finally {
                semaphore.release();
            }
        }
    }

    public void stop() {
        for (BlueContext blueContext : blueContexts.values()) {
            blueContext.stop();
        }
    }
}
