package com.xt.qc.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

/**
 */
public class PowerUtil {
    private PowerManager.WakeLock wakeLock;
    private KeyguardManager.KeyguardLock keyguardLock;

    public PowerUtil(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "OrderWakelock");
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = km.newKeyguardLock("OrderKeyguardLock");
    }

    private void acquire() {
        wakeLock.acquire(1800000);
        keyguardLock.disableKeyguard();
    }

    private void release() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
            keyguardLock.reenableKeyguard();
        }
    }

    public void handleWakeLock(boolean isWake) {
        if (isWake) {
            this.acquire();
        } else {
            this.release();
        }
    }
}
