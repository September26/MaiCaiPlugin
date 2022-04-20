package com.xt.qc.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.monkeytong.riz.R;
import com.xt.qc.services.OrderService;
import com.xt.qc.utils.LogUtil;
import com.xt.qc.utils.ShowUtil;
import com.xt.qc.utils.ToastUtil;


public class MainActivity extends Activity implements AccessibilityManager.AccessibilityStateChangeListener {

    //开关切换按钮
    private TextView pluginStatusText;
    private ImageView pluginStatusIcon;
    //AccessibilityService 管理
    private AccessibilityManager accessibilityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.logI("MainActivity");
        setContentView(R.layout.activity_main);
        pluginStatusText = (TextView) findViewById(R.id.layout_control_accessibility_text);
        pluginStatusIcon = (ImageView) findViewById(R.id.layout_control_accessibility_icon);

        handleMaterialStatusBar();

        //监听AccessibilityService 变化
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(this);
        updateServiceStatus();
    }

    /**
     * 适配MIUI沉浸状态栏
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleMaterialStatusBar() {
        // Not supported in APK level lower than 21
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(0xffE46C62);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //移除监听服务
        accessibilityManager.removeAccessibilityStateChangeListener(this);
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void openAccessibility(View view) {
        /**
         * 开启悬浮框,申请悬浮框权限
         */
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            ToastUtil.showToast(this, "请先开启悬浮窗权限！");
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 5004);
            return;
        }
        try {
            Toast.makeText(this, "点击" + getString(R.string.app_name) + pluginStatusText.getText(), Toast.LENGTH_SHORT).show();
            Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(accessibleIntent);
        } catch (Exception e) {
            Toast.makeText(this, "遇到一些问题,请手动打开系统设置>无障碍服务>" + getString(R.string.app_name), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void openSettings(View view) {
        ToastUtil.showToast(this, "该功能已经合并到了开启插件中，直接点击开启插件即可");
    }


    @Override
    public void onAccessibilityStateChanged(boolean enabled) {
        updateServiceStatus();
    }

    /**
     * 更新当前 OrderService 显示状态
     */
    private void updateServiceStatus() {
        if (isServiceEnabled()) {
            pluginStatusText.setText(R.string.service_off);
            pluginStatusIcon.setBackgroundResource(R.mipmap.ic_stop);
        } else {
            pluginStatusText.setText(R.string.service_on);
            pluginStatusIcon.setBackgroundResource(R.mipmap.ic_start);
        }
    }

    /**
     * 获取 OrderService 是否启用状态
     *
     * @return
     */
    private boolean isServiceEnabled() {
        boolean serviceRunning = ShowUtil.isServiceRunning(this, OrderService.class.getName());
        LogUtil.logI("serviceRunning:" + serviceRunning);
        return serviceRunning;
    }
}
