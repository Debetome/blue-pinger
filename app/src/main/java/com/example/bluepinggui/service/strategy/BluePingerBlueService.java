package com.example.bluepinggui.service.strategy;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.OutputStream;

public class BluePingerBlueService implements BlueService {
    public static final String TAG = "BluetoothPingerService";
    private static final int PACKET_SIZE = 600;

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
            byte[] packet = new byte[PACKET_SIZE];
            for (int j = 0; j < PACKET_SIZE; j++) {
                packet[j] = (byte) (Math.random() * 255); // Populate packet with dummy data
            }

            BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(
                    device.getUuids()[0].getUuid());
            bluetoothAdapter.cancelDiscovery();
            socket.connect();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(packet);
            outputStream.flush();

            Log.i(TAG, "Sent packet to " + bluetoothAddress);
            socket.close();

        } catch (Exception e) {
            Log.e(TAG, "Error while sending packet: " + e.getMessage());
            mainHandler.post(() -> {
                // Update UI on failure if needed
                Log.e(TAG, "Ping failed on thread " + Thread.currentThread().getId());
            });
        }
    }
}
