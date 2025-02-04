package com.example.bluepinggui.service.factory;

import android.util.Log;

import com.example.bluepinggui.service.strategy.BlueService;
import com.example.bluepinggui.service.strategy.BluePingerService;
import com.example.bluepinggui.service.strategy.BluePairingService;
import com.example.bluepinggui.service.strategy.BlueScanService;

public class BlueServiceFactory {
    private static final String TAG = "BluetoothServiceFactory";

    public static BlueService createService(BlueServiceType type) {
        switch (type) {
            case PING:
                return new BluePingerService();
            case PAIR:
                return new BluePairingService();
            case SCAN:
                return new BlueScanService();
            default:
                Log.e(TAG, "Unknown service type: " + type);
                throw new IllegalArgumentException("Invalid service type: " + type);
        }
    }
}
