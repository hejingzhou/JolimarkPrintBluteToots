package com.xinkaiyuan.printerlibrary;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * Copyright (C) 2020 jmw.com.cn Inc. All rights reserved.
 * <p>
 * Author:Created Jmw by HeJingzhou on 2020/6/22 2:17 PM
 * <p>
 * Company:北京天创时代信息技术有限公司
 * <p>
 * Email:tcoywork@163.com
 * <p>
 * Apply:蓝牙辅助类
 */
public class BlueToothHelper implements LifecycleObserver, BlueToothFindBr.OnBlueToothDeviceListener {

    private final BlueToothFindBr mBlueToothFindBr;
    private Activity mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private OnBluetoothHandlerListener onBluetoothHandlerListener;
    private int requestCodeID;


    public BlueToothHelper(Activity mContext, OnBluetoothHandlerListener onBluetoothHandlerListener) {
        this.mContext = mContext;
        this.onBluetoothHandlerListener = onBluetoothHandlerListener;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //注册广播 接收搜索到的蓝牙设备
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mBlueToothFindBr = new BlueToothFindBr(this);
        mContext.registerReceiver(mBlueToothFindBr, filter);
    }

    /**
     * 开启可被发现
     */
    public void openCanFind() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        mContext.startActivity(discoverableIntent);
    }

    /**
     * 获取蓝牙适配器
     */
    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter;
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    /**
     * 打开蓝牙
     */
    public void openBlueTooth(int requestCodeValue) {
        this.requestCodeID = requestCodeValue;
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivityForResult(enableBtIntent, requestCodeID);
            } else {
                onBluetoothHandlerListener.blueToothEnabledSuccess();
            }
        } else {
            onBluetoothHandlerListener.noSupport();
        }
    }

    /**
     * 获取已配对蓝牙列表
     */
    public void findBondedDevices() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                onBluetoothHandlerListener.bondedDevices(bondedDevices);
            }
        }
    }

    /**
     * 搜索其他蓝牙设备
     */
    public void findBlueToothDevice() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * 开始配对
     */
    public void startBonded(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            return;
        }
//        //判断设备是否配对，没有配对在配，配对了就不需要配了
        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE || bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            BluetoothGatt gatt = bluetoothDevice.connectGatt(mContext, false, new BluetoothGattCallbackData());
            boolean connect = gatt.connect();
            System.out.println("重新进行连接：" + connect);
        }
    }

    /**
     * 取消配对
     */
    public void cleanBonded(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            return;
        }
        BluetoothGatt gatt = bluetoothDevice.connectGatt(mContext, false, new BluetoothGattCallbackData());
        gatt.close();
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        if (requestCodeID == requestCode) {
            if (resultCode == RESULT_OK) {
                onBluetoothHandlerListener.blueToothEnabledSuccess();
            } else {
                onBluetoothHandlerListener.blueToothOpenError();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroy() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mBlueToothFindBr != null) {
            mBlueToothFindBr.onDestroy();
            mContext.unregisterReceiver(mBlueToothFindBr);
        }
    }

    @Override
    public void findOtherDevice(BluetoothDevice device) {
        onBluetoothHandlerListener.UnBondedDevice(device);
    }

    public interface OnBluetoothHandlerListener {

        /**
         * 设备不支持
         */
        void noSupport();

        /**
         * 蓝牙开启成功
         */
        void blueToothEnabledSuccess();

        /**
         * 蓝牙开启错误
         */
        void blueToothOpenError();

        /**
         * 已配对设备信息
         */
        void bondedDevices(Set<BluetoothDevice> bondedDevices);

        /**
         * 未配对设备
         */
        void UnBondedDevice(BluetoothDevice device);
    }
}
