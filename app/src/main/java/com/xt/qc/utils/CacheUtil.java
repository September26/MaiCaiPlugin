package com.xt.qc.utils;

import com.xt.qc.model.StateModel;

import java.util.HashMap;
import java.util.Map;

public class CacheUtil {

    private static volatile CacheUtil instance;
    //    private static Context mContext;
    private static Map<String, StateModel> map = new HashMap<>();
    private StateModel stateModel = new StateModel();

    private CacheUtil() {
    }

    public static CacheUtil getInstance() {
        if (instance == null) {
            synchronized (CacheUtil.class) {
                if (instance == null) {
                    instance = new CacheUtil();
                }
            }
        }
        return instance;
    }

    public void clear() {
        map.clear();
    }


//    public void putValue(String key, String value) {
//        map.put(key, value);
//    }
//
//    public String getValue(String key) {
//        return map.get(key);
//    }

    public StateModel getStateModel() {
        return stateModel;
    }

}