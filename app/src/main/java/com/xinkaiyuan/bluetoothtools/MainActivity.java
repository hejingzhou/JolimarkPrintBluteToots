package com.xinkaiyuan.bluetoothtools;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xinkaiyuan.printerlibrary.BlueToothHelper;
import com.xinkaiyuan.printerlibrary.PrintManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements BlueToothHelper.OnBluetoothHandlerListener {

    private static final int REQUEST_ENBLE_BT = 100;
    private static final int REQUEST_ENBLE_LT = 101;
    private BlueToothHelper mBlueToothHelper;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();

    LinearLayout mLinearBonded, mLinearNotBonded;
    private String deviceID;
    private PrintManager printerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLinearBonded = findViewById(R.id.mLinearBonded);
        mLinearNotBonded = findViewById(R.id.mLinearNotBonded);


        mBlueToothHelper = new BlueToothHelper(this, this);
        getLifecycle().addObserver(mBlueToothHelper);

        findViewById(R.id.mTvOpenBlueTooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启蓝牙
                mBlueToothHelper.openBlueTooth(REQUEST_ENBLE_BT);
            }
        });
        findViewById(R.id.mTvCanFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启可发现
                mBlueToothHelper.openCanFind();
            }
        });
        findViewById(R.id.mTvFindPei).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查找已配对的设备
                mLinearBonded.removeAllViews();
                mBlueToothHelper.findBondedDevices();
            }
        });
        findViewById(R.id.mTvFindUnPei).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发现更多设备
                mLinearNotBonded.removeAllViews();
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mDeviceList.clear();
                    mBlueToothHelper.findBlueToothDevice();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENBLE_LT);
                }
            }
        });
        findViewById(R.id.mTvPrint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行打印
                mBlueToothHelper.getBluetoothAdapter().cancelDiscovery();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("请选择连接方式");
                builder.setMessage("请选择是否需要开启防丢单功能(开启之后智能关机复原)");
                builder.setPositiveButton("开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printerManager = PrintManager.getInstances().init(true, deviceID);
                        printerManager.connect();
                    }
                });
                builder.setNegativeButton("不开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printerManager = PrintManager.getInstances().init(false, deviceID);
                        printerManager.connect();
                    }
                });
                builder.show();
            }
        });

        findViewById(R.id.mTvPrintModel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (printerManager != null) {
                    Toast.makeText(MainActivity.this, printerManager.getPrinterModel(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.mTvPrintText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printerManager.print(MainActivity.this,"");
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_ENBLE_LT == requestCode) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //发现更多设备
                mBlueToothHelper.findBlueToothDevice();
            } else {
                Toast.makeText(this, "请您打开位置权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mBlueToothHelper.activityResult(requestCode, resultCode, data);
    }

    @Override
    public void noSupport() {
        Toast.makeText(this, "该设备不支持蓝牙", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void blueToothEnabledSuccess() {
        Toast.makeText(this, "打开蓝牙成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void blueToothOpenError() {
        Toast.makeText(this, "开启错误 请您打开蓝牙", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bondedDevices(Set<BluetoothDevice> bondedDevices) {
        for (BluetoothDevice bondedDevice : bondedDevices) {
            if (bondedDevice.getName().contains("560K")) {
                deviceID = bondedDevice.getAddress();
            }
            System.out.println("已配对设备：" + bondedDevice.getName());
            TextView textView = new TextView(this);
            textView.setText("已配对->" + bondedDevice.getName());
            textView.setTag(bondedDevice);
            textView.setTextSize(20);
            textView.setTextColor(Color.parseColor("#990033"));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) v.getTag();
                    mBlueToothHelper.startBonded(bluetoothDevice);
                }
            });
            mLinearBonded.addView(textView);
        }
    }

    @Override
    public void UnBondedDevice(BluetoothDevice device) {
        if (device.getName().contains("560K")) {
            deviceID = device.getAddress();
        }
        System.out.println("发现的新设备：" + device.getName() + "\t" + device.getAddress());
        if (!mDeviceList.contains(device)) {
            mDeviceList.add(device);
            TextView textView = new TextView(this);
            textView.setText("新设备->" + device.getName());
            textView.setTag(device);
            textView.setTextSize(20);
            textView.setTextColor(Color.parseColor("#aacc00"));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) v.getTag();
                    mBlueToothHelper.startBonded(bluetoothDevice);
                }
            });
            mLinearNotBonded.addView(textView);
        }

    }
}
