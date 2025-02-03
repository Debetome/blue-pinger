package com.example.bluepinggui.target.profile;

import com.example.bluepinggui.service.factory.BlueServiceType;

/*
** Usage: **

TargetProfile targetProfile = new TargetProfileBuilder()
            .bluetoothAddress("00:00:00:00:00:00")
            .threadCount(30)
            .services(new BlueServiceType[] { BlueServiceType.PING, BlueServiceType.PAIR })
            .build();

 */

public class TargetProfileBuilder {
    private String bluetoothAddress;
    private int threadCount = 1;
    private BlueServiceType[] services;

    public TargetProfileBuilder bluetoothAddress(String bluetoothAddress) {
        this.bluetoothAddress = bluetoothAddress;
        return this;
    }

    public TargetProfileBuilder threadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public TargetProfileBuilder services(BlueServiceType[] services) {
        this.services = services;
        return this;
    }

    public TargetProfile build() {
        return new TargetProfile(this);
    }

    // TargetProfileBuilder getters
    public String getBluetoothAddress() {
        return this.bluetoothAddress;
    }

    public int getThreadCount() {
        return this.threadCount;
    }

    public BlueServiceType[] getServices() {
        return this.services;
    }
}
