package com.example.bluepinggui.service.strategy;

import android.content.Context;
import android.os.Handler;

public interface BlueService {
    void execute(Context context, String bluetoothAddress, Handler mainHandler);
}
