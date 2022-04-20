package com.xt.maicai;

import android.app.Activity;

import java.util.Stack;

public class RecordUtil {

    private static RecordUtil instance = new RecordUtil();
    private Stack<Activity> stack = new Stack<>();

    private RecordUtil() {
    }

    public static RecordUtil getInstance() {
        return instance;
    }


    public Stack<Activity> getStack() {
        return stack;
    }
}