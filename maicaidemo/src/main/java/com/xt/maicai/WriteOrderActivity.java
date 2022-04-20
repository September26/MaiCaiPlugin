package com.xt.maicai;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class WriteOrderActivity extends Activity {

    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecordUtil.getInstance().getStack().add(this);
        setContentView(R.layout.layout_write);
        findViewById(R.id.topay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (random.nextInt(100) > 95) {
                    //成功跳转支付
                    Intent intent = new Intent(WriteOrderActivity.this, PayActivity.class);
                    startActivity(intent);
                } else {
                    //失败弹加载失败的框
                    final Dialog dialog = new Dialog(WriteOrderActivity.this);
                    dialog.setTitle("加载失败，请重新尝试");
                    dialog.setContentView(R.layout.fail_dialog);
                    dialog.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.back_to).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecordUtil.getInstance().getStack().pop();
    }


}