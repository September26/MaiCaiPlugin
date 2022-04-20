package com.xt.qc.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.xt.qc.utils.LogUtil;

import java.util.List;

public class ShowUtil {
    public static final String TAG = "ShowUtil";

    public static boolean isServiceRunning(Context context, String serviceName) {
        //校验服务是否还活着
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static Pair<String, String> getForegroundActivity(Context context, AccessibilityEvent event) {
        return new Pair<>(event.getPackageName().toString(), event.getClassName().toString());
    }

    public static void setViewTouchGrag(final View touchView, final View floatView, final WindowManager.LayoutParams layoutParams, final WindowManager windowManager, final boolean isWelt) {

        touchView.setLongClickable(false);
        final int width = windowManager.getDefaultDisplay().getWidth();
        final int height = windowManager.getDefaultDisplay().getHeight();
        touchView.setOnTouchListener(new View.OnTouchListener() {
            boolean isMove = false;
            int lastX;
            int lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int rawX = (int) event.getRawX();
                int rawY = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        LogUtil.logI(TAG, "onTouch:ACTION_UP,x=" + rawX + ",y=" + rawY);
                        return isMove;
                    case MotionEvent.ACTION_DOWN:
                        //贴边显示
                        isMove = false;
                        lastX = rawX;
                        lastY = rawY;
                        LogUtil.logI(TAG, "onTouch:ACTION_DOWN,x=" + rawX + ",y=" + rawY);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        if (lastX == rawX && lastY == rawY) {
                            return false;
                        }
                        isMove = true;
                        layoutParams.x = width - rawX;
                        layoutParams.y = height - rawY - floatView.getMeasuredHeight() / 2;
                        LogUtil.logI(TAG, "onTouch:ACTION_MOVE,x=" + rawX + ",y=" + rawY);
                        LogUtil.logI(TAG, "onTouch:ACTION_MOVE, layoutParams.x=" + layoutParams.x + ", layoutParams.y=" + layoutParams.y);
                        updateXY();
                        return true;
                }
                return false;
            }

            private void updateXY() {
                try {
                    LogUtil.logI(TAG, "updateViewLayout,x=" + layoutParams.x + ",y=" + layoutParams.y);
                    windowManager.updateViewLayout(floatView, layoutParams);
                } catch (Exception e) {
                    Log.e(TAG, "onTouch: ");
                }
            }
        });
    }
}
