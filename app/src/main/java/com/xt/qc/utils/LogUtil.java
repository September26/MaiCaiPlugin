package com.xt.qc.utils;

import android.util.Log;


public class LogUtil {
    public static String TAG = "maicailog";
    public static boolean isDebug = false;//正式包则关闭log功能。打log也会拖慢运行速度

    public static void logI(String message) {
        logI(TAG, message);
    }

    public static void logI(String tag, String message) {
        if (isDebug) {
            return;
        }
        Log.i(TAG + tag, message);
    }
}
