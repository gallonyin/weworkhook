package org.gallonyin.weworkhk;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gallonyin on 2018/6/13.
 */

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        try {
            XposedBridge.log("handleLoadPackage: " + lpparam.packageName + "    " + lpparam.processName);

            if (lpparam.packageName.equals("com.tencent.wework")) {
                Log.d("Main", "wework");
                new WeWork().start(lpparam.classLoader);
            }
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            XposedBridge.log(sw.toString());
        }
    }

}
