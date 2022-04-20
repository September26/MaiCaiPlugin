package com.xt.qc.model;

public class StateModel {

    /**
     * 抢单模式
     */
    public int execType = 0;
    /**
     * 是否开启抢单
     */
    public boolean isStart = false;


    /**
     * 订单状态
     * 初始化=0
     * 购物车页面=1
     * 确认支付页面时，加在购物车失败=2
     * 确认支付页面时，加在购物车成功=3
     */
    public int currentState = 0;

    public interface OrderState {
        int INIT = 0;
        int GET_ELEMENT = 1;
        int LOAD_FAIL = 2;
        int TO_PAY = 3;
        int PAY_SUCESS = 4;
    }
}
