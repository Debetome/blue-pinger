package com.example.bluepinggui.service.strategy;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class BlueScanService implements BlueService {
    private static final String TAG = "BlueScanService";
    private BroadcastReceiver scanReceiver;

    @SuppressLint("MissingPermission")
    @Override
    public void execute(Context context, String bluetoothAddress, Handler mainHandler) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth not supported on this device.");
            return;
        }

        // Register the broadcast receiver to listen for discovered devices
        if (scanReceiver == null) {  // Avoid multiple registrations
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            scanReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (device != null) {
                            Log.i(TAG, "Discovered device: " + device.getAddress());
                            // Notify the main thread (UI) if needed
                            mainHandler.post(() -> {
                                Log.i(TAG, "Device found on main thread: " + device.getAddress());
                            });
                        }
                    }
                }
            };
            context.registerReceiver(scanReceiver, filter);

            // Register for discovery finished action to unregister the receiver
            IntentFilter finishFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG, "Bluetooth discovery finished.");
                    if (scanReceiver != null) {
                        try {
                            context.unregisterReceiver(scanReceiver);  // Unregister scanReceiver
                        } catch (IllegalArgumentException e) {
                            Log.w(TAG, "Receiver not registered: " + e.getMessage());
                        }
                    }
                }
            }, finishFilter);

            // Start Bluetooth discovery
            if (!bluetoothAdapter.isDiscovering()) {
                Log.i(TAG, "Starting Bluetooth scan...");
                bluetoothAdapter.startDiscovery();
            }
        }
    }
}
