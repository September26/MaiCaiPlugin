package com.xt.maicai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecordUtil.getInstance().getStack().add(this);
        setContentView(R.layout.layout_main);
        ((TextView) findViewById(R.id.getelement)).setText("去结算(3)");
        findViewById(R.id.getelement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WriteOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecordUtil.getInstance().getStack().pop();
    }
}
