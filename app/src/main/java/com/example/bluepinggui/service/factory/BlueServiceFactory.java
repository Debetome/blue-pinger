package com.example.bluepinggui.service.factory;

import android.util.Log;

import com.example.bluepinggui.service.strategy.BlueService;
import com.example.bluepinggui.service.strategy.BluePingerService;
import com.example.bluepinggui.service.strategy.BluePairingBlueService;
import com.example.bluepinggui.service.strategy.BlueScanBlueService;

public class BlueServiceFactory {
    private static final String TAG = "BluetoothServiceFactory";

    public static BlueService createService(BlueServiceType type) {
        switch (type) {
            case PING:
                return new BluePingerService();
            case PAIR:
                return new BluePairingBlueService();
            case SCAN:
                return new BlueScanBlueService();
            default:
                Log.e(TAG, "Unknown service type: " + type);
                throw new IllegalArgumentException("Invalid service type: " + type);
        }
    }
}
