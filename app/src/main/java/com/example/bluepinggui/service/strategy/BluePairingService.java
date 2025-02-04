package com.example.bluepinggui.service.strategy;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class BluePairingService implements BlueService {
    private static final String TAG = "BluetoothPairingService";

    @SuppressLint("MissingPermission")
    @Override
    public void execute(Context context, String bluetoothAddress, Handler mainHandler) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth not supported on this device.");
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bluetoothAddress);
        if (device == null) {
            Log.e(TAG, "Invalid Bluetooth address: " + bluetoothAddress);
            return;
        }

        try {
            boolean isPaired = device.createBond(); // This tries to pair with the device
            if (isPaired) {
                Log.i(TAG, "Successfully initiated pairing with " + bluetoothAddress);
            } else {
                Log.e(TAG, "Pairing failed with " + bluetoothAddress);
            }

            // Simulate delay between pair attempts (adjust as needed)
            long randomDelay = 10 + (long) (Math.random() * 30);
            Thread.sleep(randomDelay);
        } catch (Exception e) {
            Log.e(TAG, "Error while pairing: " + e.getMessage());
            mainHandler.post(() -> {
                // Update UI on failure if needed
                Log.e(TAG, "Pairing failed on thread " + Thread.currentThread().getId());
            });
        }
    }
}
