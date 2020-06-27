package com.xinkaiyuan.printerlibrary;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2020 jmw.com.cn Inc. All rights reserved.
 * <p>
 * Author:Created Jmw by HeJingzhou on 2020/6/22 2:57 PM
 * <p>
 * Company:北京天创时代信息技术有限公司
 * <p>
 * Email:tcoywork@163.com
 * <p>
 * Apply:
 */
public class BlueToothFindBr extends BroadcastReceiver {
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private OnBlueToothDeviceListener onBlueToothDeviceListener;
    private String TAG = getClass().getSimpleName();

    public BlueToothFindBr(OnBlueToothDeviceListener onBlueToothDeviceListener) {
        this.onBlueToothDeviceListener = onBlueToothDeviceListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println(action);
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = device.getName();
            String address = device.getAddress();
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address)) {
                onBlueToothDeviceListener.findOtherDevice(device);
            }
        }
    }

    public void onDestroy() {
        deviceList.clear();
    }

    public interface OnBlueToothDeviceListener {
        void findOtherDevice(BluetoothDevice device);
    }
}
