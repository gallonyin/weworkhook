package org.gallonyin.weworkhk

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Hook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        Log.d("Main", "handleLoadPackage")
        XposedBridge.log("handleLoadPackage: " + lpparam!!.packageName + "    " + lpparam.processName)
        if (lpparam.packageName == "com.tencent.wework") {
            Log.d("Main", "wework")
            WeWork().start(lpparam.classLoader)
        }
        if (lpparam.packageName == "org.gallonyin.weworkhk") {
            Log.d("Main", "weworkhk")
            XposedHelpers.findAndHookMethod(
                "org.gallonyin.weworkhk.MainActivity",
                lpparam.classLoader,
                "confirm",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        param.result = true
                    }
                })
        }
    }
}