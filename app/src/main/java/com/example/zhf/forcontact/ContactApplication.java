package com.example.zhf.forcontact;

import android.app.*;

import com.baidu.mapapi.*;

/**
 * Created by zhf on 2017/11/3.
 */

public class ContactApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
