package com.xt.maicai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Random;
import java.util.Stack;

public class PayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecordUtil.getInstance().getStack().add(this);
        setContentView(R.layout.layout_pay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecordUtil.getInstance().getStack().pop();
    }

    public void clickFinish(View v) {
        Stack<Activity> stack = RecordUtil.getInstance().getStack();
        for (Activity activity : stack) {
            activity.finish();
        }
    }
}