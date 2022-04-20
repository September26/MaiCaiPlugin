package com.xt.qc.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.monkeytong.riz.R;
import com.xt.qc.model.StateModel;
import com.xt.qc.utils.CacheUtil;
import com.xt.qc.utils.IOHelper;
import com.xt.qc.utils.LogUtil;
import com.xt.qc.utils.PowerUtil;
import com.xt.qc.utils.ShowUtil;

import java.util.ArrayList;
import java.util.List;

public class OrderService extends AccessibilityService {

    public String TAG = OrderService.class.getSimpleName();

//    interface Constant {
//        String packageName = "com.xt.maicai";
//        String ShoppingCartActivity = "com.xt.maicai.HomeActivity";
//        String ConfirmOrderActivity = "com.xt.maicai.WriteOrderActivity";
//    }

    //dingdong
    interface Constant {
        String packageName = "com.yaya.zone";
        String ShoppingCartActivity = "com.yaya.zone.home.HomeActivity";
        String ConfirmOrderActivity = "cn.me.android.cart.activity.WriteOrderActivity";
    }

    private final StateModel stateModel = CacheUtil.getInstance().getStateModel();
    private String currentActivityName = "";//当前activity的名称

    private AccessibilityNodeInfo rootNodeInfo;
    private PowerUtil powerUtil;
    private View floatView;
    private WindowManager mWindowManager;
    private ClickDownTimer clickDownTimer = new ClickDownTimer(60000, 100);


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.logI(TAG, "OrderService onCreate()");
        //初始化WindowManager对象和LayoutInflater对象
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        showFloat();
    }

    private void showFloat() {
        floatView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.showcar_login_btn, null);
        final View launcherButton = floatView.findViewById(R.id.bt_showcar_launcher);
        launcherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateModel.isStart = !stateModel.isStart;
                launcherButton.setSelected(!launcherButton.isSelected());
                LogUtil.logI(TAG, "onClick");
            }
        });
        if (mWindowManager != null) {
            try {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.width = 100;
                layoutParams.height = 100;
                layoutParams.x = 0;
                layoutParams.y = 500;
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                //设置背景透明
//        mShowLogoutLayoutParams.format = PixelFormat.TRANSLUCENT;
                layoutParams.format = PixelFormat.RGBA_8888;
                layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                }
                ShowUtil.setViewTouchGrag(launcherButton, floatView, layoutParams, mWindowManager, false);
                mWindowManager.addView(floatView, layoutParams);
            } catch (Exception e) {
                Log.e(TAG, "showLauncherBtn: e =", e);
            }
        }
    }


    /**
     * AccessibilityEvent
     *
     * @param event 事件
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        //非指定包名的不拦截
        if (!Constant.packageName.equals(event.getPackageName().toString())) {
            return;
        }
        currentActivityName = ShowUtil.getForegroundActivity(getApplicationContext(), event).second;
        LogUtil.logI("getCurrgetentActivityName,currentActivityName:" + currentActivityName);
        if (!stateModel.isStart) {
            return;
        }
        LogUtil.logI("currentActivityName:" + currentActivityName);
        //1.如果用户进入到了购物车页面，则我们使用无障碍辅助帮助用户点击“去结算”按钮（PS：即使页面显示的是去结算(N)，查找“去结算”也是可以查到的）。
        //todo 一直执行，直到进入下一个状态。
        //todo 如果页面不切换，就一直点击去结算
        if (Constant.ShoppingCartActivity.equals(currentActivityName)) {
            stateModel.currentState = StateModel.OrderState.GET_ELEMENT;
            //疯狂点击 "去结算"
            clickDownTimer.startClick("去结算", StateModel.OrderState.TO_PAY);
            return;
        }

        //2.如果用户进入到了订单确认页面，则我们使用无障碍辅助帮助用户点击“立即支付”按钮。
        if (Constant.ConfirmOrderActivity.equals(currentActivityName)) {
            stateModel.currentState = StateModel.OrderState.TO_PAY;
            //如果存在重新加载，则点击重新加载，否则点击立即支付。

            //todo 进入页面，加载失败，则需要一直点击重新加载

            /**
             * 分为两种操作方式
             * 1.立即支付疯狂点击
             * 2.购物车和确认订单页面来回点
             */
            //todo 前方拥挤，请稍厚再试，则一直点击；如果
            clickDownTimer.startClick("立即支付", StateModel.OrderState.PAY_SUCESS);
            return;
        }
        if (stateModel.currentState <= StateModel.OrderState.GET_ELEMENT) {
            stateModel.currentState = StateModel.OrderState.INIT;
            return;
        }
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            return;
        }
        stateModel.currentState = StateModel.OrderState.LOAD_FAIL;
        //3.如果用户在订单确认页面点击立即支付，弹出了加载失败，请重新尝试的按钮，则我们使用无障碍辅助帮助用户点击“重新加载”按钮。
        List<AccessibilityNodeInfo> reloadList = rootInActiveWindow.findAccessibilityNodeInfosByText("重新加载");
        if (reloadList.size() > 0) {
            clickDownTimer.startClick("重新加载", StateModel.OrderState.TO_PAY);
        }
        //4.最后记录一下当面页面的节点状态，并持久化，方便后续排产问题。
        List<String> list = new ArrayList<>();
        list.add(rootInActiveWindow.toString());
        IOHelper.write2File(getApplicationContext(), list);
    }

    private void watchFlagsFromPreference() {
        this.powerUtil = new PowerUtil(this);
        this.powerUtil.handleWakeLock(true);
    }


    class ClickDownTimer extends CountDownTimer {
        String text;
        int expectState;

        ClickDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void startClick(String text, int expectState) {
            LogUtil.logI("click:" + text);
            this.text = text;
            this.expectState = expectState;
            start();
        }


        @Override
        public void onTick(long millisUntilFinished) {
            rootNodeInfo = getRootInActiveWindow();
            if (rootNodeInfo == null) {
                return;
            }
            List<AccessibilityNodeInfo> gettlement = rootNodeInfo.findAccessibilityNodeInfosByText(text);
            LogUtil.logI("find:" + text + ",result:" + gettlement.size() + ",expectState:" + expectState + ",stateModel.currentState:" + stateModel.currentState);
            if (stateModel.currentState >= expectState) {
                clickDownTimer.cancel();
                return;
            }
            if (gettlement.size() > 0) {
                gettlement.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

        @Override
        public void onFinish() {

        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        this.watchFlagsFromPreference();
    }

    @Override
    public void onDestroy() {
        this.powerUtil.handleWakeLock(false);
        super.onDestroy();
        clickDownTimer.cancel();
    }

}

