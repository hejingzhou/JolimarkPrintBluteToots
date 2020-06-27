package com.xinkaiyuan.printerlibrary;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Copyright (C) 2020 jmw.com.cn Inc. All rights reserved.
 * <p>
 * Author:Created Jmw by HeJingzhou on 2020/6/23 2:01 PM
 * <p>
 * Company:北京天创时代信息技术有限公司
 * <p>
 * Email:tcoywork@163.com
 * <p>
 * Apply:
 */
public class PinBlueBr extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {

        }
    }
}
