package org.gallonyin.wechathk;

import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gallonyin on 2018/6/13.
 */

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage: " + lpparam.packageName);

        if (lpparam.packageName.equals("com.tencent.wework")) {
            Log.d("Main", "wework");
            new WeWork().start(lpparam.classLoader);
        }
    }

}
