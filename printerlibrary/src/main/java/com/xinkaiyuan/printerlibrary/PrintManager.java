package com.xinkaiyuan.printerlibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jolimark.printerlib.RemotePrinter;
import com.jolimark.printerlib.VAR;
import com.jolimark.printerlib.util.ByteArrayUtils;

/**
 * Copyright (C) 2020 jmw.com.cn Inc. All rights reserved.
 * <p>
 * Author:Created Jmw by HeJingzhou on 2020/6/27 11:50 AM
 * <p>
 * Company:北京天创时代信息技术有限公司
 * <p>
 * Email:tcoywork@163.com
 * <p>
 * Apply:
 */
public class PrintManager {
    private String TAG = getClass().getSimpleName();
    private RemotePrinter mPrinter;
    public static PrintManager printManager;
    private boolean lossPrevention;
    private String macAddress;
    private VAR.PrinterType printerType;

    private PrintManager() {
    }

    public static PrintManager getInstances() {
        if (printManager == null) {
            printManager = new PrintManager();
        }
        return printManager;
    }

    /**
     * 初始化
     *
     * @param lossPrevention 是否开启防丢失模式
     * @param macAddress     蓝牙地址
     */
    public PrintManager init(boolean lossPrevention, String macAddress) {
        this.lossPrevention = lossPrevention;
        this.macAddress = macAddress;
        mPrinter = new RemotePrinter(VAR.TransType.TRANS_BT, macAddress);
        mPrinter.close();
        return this;
    }

    /**
     * 进行打印机连接
     */
    public void connect() {
        new ConnectAsyncTask().execute();
    }

    private class ConnectAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                if (mPrinter.isConnected()) {
                    // TODO: 2020/6/27 已连接无需再次连接
                    Log.i(TAG, "connect: 已连接无需再次连接");
                    return true;
                }
                boolean openResult = mPrinter.open(lossPrevention);
                Log.i(TAG, "doInBackground: " + openResult);
                if (openResult) {
                    printerType = mPrinter.getPrinterType();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public String getPrinterModel() {
        if (printManager != null) {
            return mPrinter.getPrinterModel();
        }
        return "";
    }

    /**
     * 进行打印
     *
     * @param data
     */
    public void print(Context context,String data) {
        mPrinter.sendData(getTextByteData(context,data));
    }

    //获取文本数据
    public  byte[] getTextByteData(Context context,String data) {
        VAR.PrinterType printerType = mPrinter.getPrinterType();
        String locale = java.util.Locale.getDefault().getDisplayName();
        // 数据容器strToByte[]
        byte strToByte[] = null;
        // 打印机初始化
        strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a17);

        String str = "文本打印示例：\r\n\r\n";
        String chineseContent = "中文：欢迎使用无线打印机！\r\n";
        String englishContent = "ENGLISH:Welcome to use the  wireless printer!\r\n\r\n";
        System.out.println("-------------类型：" + printerType);

        if (printerType == VAR.PrinterType.PT_DOT24) { // PT_DOT24支持的打印机指令
            // 打印中文，需要发送打印中文指令，0x1C 0x26是打印中文的指令
            // twoToOne()函数是byte类型数组连接的工具类
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a14);
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(str));
            // 打印默认字体
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(context.getString(R.string.default_typeface) + "\r\n"));
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
//			// 打印斜体
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte("斜体：\r\n"));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a18);
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a19);
//             打印粗体
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte("粗体：\r\n"));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a20);
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a21);
//             重叠打印
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte("重叠打印：\r\n"));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a22);
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a23);
//             下划线 一条实线
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte("下划线一条实线：\r\n"));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a24);
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a26);
//             下划线 一条虚线
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte("下划线一条虚线：\r\n"));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a25);
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a26);
			// 倍宽打印
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte("倍宽打印：\r\n"));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a27);
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
			strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a28);
            // 倍高倍宽
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte("倍高倍宽打印：\r\n"));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a29);
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
//			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a30);
            // 倍高
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte("倍高打印：\r\n"));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a31);
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
//			strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a32);
            // 发送取消打印中文指令 0x1c 0x2e
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a15);
        } else if (printerType == VAR.PrinterType.PT_THERMAL || printerType == VAR.PrinterType.PT_DOT9) {
            // 默认模式
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a14);// 打印中文，需要发送打印中文指令，0x1C
            // 0x26是打印中文的指令
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(context.getString(R.string.default_typeface) + "\r\n"));
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
            // strToByte = ArrayUtils.twoToOne(strToByte, a15);// 发送取消打印中文指令
            // 0x1c 0x2e
            // 倍宽
            // strToByte = ArrayUtils.twoToOne(strToByte, a14);//中文打印模式
            if (locale.contains("中国")) {
                strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b1);
            } else {
                strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b4);
            }
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(context.getString(R.string.double_width) + "\r\n"));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b1);
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a15);// 取消中文打印模式
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b4);
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
            // 倍高

            if (locale.contains("中国")) {
                strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a14);// 中文打印模式
                strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b2);
            } else {
                strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b5);
            }
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(context.getString(R.string.double_height) + "\r\n"));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a14);// 中文打印模式
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b2);
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a15);// 取消中文打印模式
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b5);
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
            // 倍宽、倍高字体
            if (locale.contains("中国")) {
                strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a14);// 中文打印模式
                strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b3);
            } else {
                strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b6);
            }
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(context.getString(R.string.double_height_and_double_width) + "\r\n"));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a14);// 中文打印模式
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b3);
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(chineseContent));
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a15);// 取消中文打印模式
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b6);
            strToByte = ByteArrayUtils.twoToOne(strToByte, ByteArrayUtils.stringToByte(englishContent));
            // 取消倍宽倍高模式
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b11);
            strToByte = ByteArrayUtils.twoToOne(strToByte, Command.b12);
        }
        // 打印机初始化
        strToByte = ByteArrayUtils.twoToOne(strToByte, Command.a17);
        strToByte = ByteArrayUtils.twoToOne(strToByte, strToByte);
        return strToByte;
    }

}