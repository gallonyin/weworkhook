package org.gallonyin.weworkhk;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import org.gallonyin.weworkhk.util.SignUtils;

/**
 * Created by gallon on 2018/3/26.
 */

public class MyApplication extends Application {

    public static MyApplication INSTANCE;
    public static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        sp = getSharedPreferences("sp", MODE_PRIVATE);
    }

}
