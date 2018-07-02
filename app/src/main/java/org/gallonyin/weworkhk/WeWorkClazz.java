package org.gallonyin.weworkhk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.SystemClock;
import android.telephony.CellLocation;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.log;

/**
 * Created by gallonyin on 2018/6/27.
 */

public class WeWorkClazz {

    private static Class<?> ParamClass;
    private static Class<?> AttendanceActivity2Class;

    public static Class<?> getParamClass() {
        if (ParamClass == null) {
            ParamClass = XposedHelpers.findClass("com.tencent.wework.enterprise.attendance.controller.AttendanceActivity2$Param", WeWork.classLoader);
        }
        return ParamClass;
    }

    public static Class<?> getAttendanceActivity2Class() {
        if (AttendanceActivity2Class == null) {
            AttendanceActivity2Class = XposedHelpers.findClass("com.tencent.wework.enterprise.attendance.controller.AttendanceActivity2", WeWork.classLoader);
        }
        return AttendanceActivity2Class;
    }

}
