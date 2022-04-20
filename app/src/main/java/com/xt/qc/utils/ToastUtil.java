package com.xt.qc.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * toast显示
 * 后续考虑Snackbar替代
 */
public class ToastUtil {
    public static void showToast(Context context, String tip) {
        Toast toast = Toast.makeText(context, tip, Toast.LENGTH_LONG);
        toast.show();
    }


}
