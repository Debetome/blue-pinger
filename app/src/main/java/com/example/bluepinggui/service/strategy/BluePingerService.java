package com.example.bluepinggui.service.strategy;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BluePingerService implements BlueService {
    public static final String TAG = "BluetoothPingerService";
    private static final int PACKET_SIZE = 600;

    private final List<UUID> uuids = new ArrayList<>();
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Executor for delayed tasks

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

        // If UUIDs are available, connect immediately
        if (!uuids.isEmpty() || (device.getUuids() != null && device.getUuids().length != 0)) {
            connectToDevice(device, bluetoothAdapter, mainHandler);
            return;
        }

        Log.i(TAG, "Fetching UUIDs...");
        if (device.fetchUuidsWithSdp()) {
            connectToDevice(device, bluetoothAdapter, mainHandler);
        }

        Log.e(TAG, "Failed to fetch UUIDs.");
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, Handler mainHandler) {
        try {
            byte[] packet = new byte[PACKET_SIZE];
            for (int j = 0; j < PACKET_SIZE; j++) {
                packet[j] = (byte) random.nextInt(256); // Populate packet with dummy data
            }

            BluetoothSocket socket;
            if (uuids.isEmpty()) {
                ParcelUuid[] parcelUuids = device.getUuids();
                if (parcelUuids == null || parcelUuids.length == 0) {
                    Log.e(TAG, "No UUIDs found for device.");
                    return;
                }

                for (ParcelUuid parcelUuid : parcelUuids) {
                    uuids.add(parcelUuid.getUuid());
                }

                socket = device.createInsecureRfcommSocketToServiceRecord(uuids.get(0));
            } else {
                UUID randomUUID = uuids.get(random.nextInt(uuids.size()));
                socket = device.createInsecureRfcommSocketToServiceRecord(randomUUID);
            }

            if (uuids.get(0) != null) {
                Log.d(TAG, "UUIDs available");
            }

            bluetoothAdapter.cancelDiscovery();
            socket.connect();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(packet);
            outputStream.flush();

            Log.i(TAG, "Sent packet to " + device.getAddress());
            socket.close();

        } catch (Exception e) {
            Log.e(TAG, "Error while sending packet: " + e.getMessage());
            mainHandler.post(() -> Log.e(TAG, "Ping failed on thread " + Thread.currentThread().getId()));
        }
    }
}
