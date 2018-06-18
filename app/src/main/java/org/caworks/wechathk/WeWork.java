package org.caworks.wechathk;

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
import android.os.SystemClock;
import android.telephony.CellLocation;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by gallonyin on 2018/6/13.
 */

public class WeWork {
    private static final String TAG = "WeWork";

    private ClassLoader classLoader;

    private float salt = 0.00005f;
    private float defLa = 32.041713f;
    private float defLo = 118.784308f;
    private float la = 0;
    private float lo = 0;

    public void start(ClassLoader classLoader) {
        this.classLoader = classLoader;

        hkStart();
    }

    private void initReceiver(Context context, final SharedPreferences sp) {
        BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("data");
                Log.e(TAG, "onReceive: " + data);
                String[] split = data.split("#");
                if (split.length != 2) return;
                la = Float.parseFloat(split[0]);
                lo = Float.parseFloat(split[1]);
                sp.edit().putFloat("GPSLatitude", la)
                        .putFloat("GPSLongitude", lo)
                        .apply();
            }
        };

        IntentFilter filter = new IntentFilter("weworkdk");
        context.registerReceiver(receiver, filter);
    }

    private void hkStart() {
        XposedHelpers.findAndHookMethod("android.app.Application", classLoader, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];

                SharedPreferences sp = context.getSharedPreferences("hkWeWork", Context.MODE_PRIVATE);
                la = sp.getFloat("GPSLatitude", defLa);
                lo = sp.getFloat("GPSLongitude", defLo);

                hkGPS(classLoader);
                initReceiver(context, sp);
            }
        });
    }

    private float salted(float f) {
        if (f > 0) {
            return (float) (f + salt * (1 - (Math.random() * 2)));
        }
        return f;
    }

    private void hkGPS(ClassLoader classLoader) {
        Log.d(TAG, "hkGPS: " + la + "#" + lo);

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                "getCellLocation", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                        Log.d(TAG, "getCellLocation");
                    }
                });

        XposedHelpers.findAndHookMethod("android.telephony.PhoneStateListener", classLoader,
                "onCellLocationChanged", CellLocation.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                        Log.d(TAG, "onCellLocationChanged");
                    }
                });

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getPhoneCount", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(1);
                            Log.d(TAG, "getPhoneCount");
                        }
                    });
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getNeighboringCellInfo", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(new ArrayList<>());
                            Log.d(TAG, "getNeighboringCellInfo");
                        }
                    });
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getAllCellInfo", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(null);
                            Log.d(TAG, "getAllCellInfo");
                        }
                    });
            XposedHelpers.findAndHookMethod("android.telephony.PhoneStateListener", classLoader,
                    "onCellInfoChanged", List.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(null);
                            Log.d(TAG, "onCellInfoChanged");
                        }
                    });
        }

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiManager", classLoader, "getScanResults", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(new ArrayList<>());
                Log.d(TAG, "getScanResults");
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiManager", classLoader, "getWifiState", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(1);
                Log.d(TAG, "getWifiState");
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiManager", classLoader, "isWifiEnabled", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
                Log.d(TAG, "isWifiEnabled");
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo", classLoader, "getMacAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("00-00-00-00-00-00-00-00");
                Log.d(TAG, "getMacAddress");
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo", classLoader, "getSSID", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("null");
                Log.d(TAG, "getSSID");
            }
        });

        XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo", classLoader, "getBSSID", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("00-00-00-00-00-00-00-00");
                Log.d(TAG, "getBSSID");
            }
        });


        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "getTypeName", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult("WIFI");
                        Log.d(TAG, "getTypeName");
                    }
                });
        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "isConnectedOrConnecting", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                        Log.d(TAG, "isConnectedOrConnecting");
                    }
                });

        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "isConnected", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                        Log.d(TAG, "isConnected");
                    }
                });

        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "isAvailable", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                        Log.d(TAG, "isAvailable");
                    }
                });

        XposedHelpers.findAndHookMethod("android.telephony.CellInfo", classLoader,
                "isRegistered", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                        Log.d(TAG, "isRegistered");
                    }
                });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(salted(la));
                l.setLongitude(salted(lo));
                l.setAccuracy(100f);
                l.setTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                }
                param.setResult(l);
                Log.d(TAG, "getLastLocation");
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastKnownLocation", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(salted(la));
                l.setLongitude(salted(lo));
                l.setAccuracy(100f);
                l.setTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                }
                param.setResult(l);
                Log.d(TAG, "getLastKnownLocation");
            }
        });


        XposedBridge.hookAllMethods(LocationManager.class, "getProviders", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("gps");
                param.setResult(arrayList);
                Log.d(TAG, "getProviders");
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getBestProvider", Criteria.class, Boolean.TYPE, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("gps");
                Log.d(TAG, "getBestProvider");
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "addGpsStatusListener", GpsStatus.Listener.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0] != null) {
                    XposedHelpers.callMethod(param.args[0], "onGpsStatusChanged", 1);
                    XposedHelpers.callMethod(param.args[0], "onGpsStatusChanged", 3);
                }
                Log.d(TAG, "addGpsStatusListener");
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "addNmeaListener", GpsStatus.NmeaListener.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);
                Log.d(TAG, "addNmeaListener");
            }
        });

        XposedHelpers.findAndHookMethod("android.location.LocationManager", classLoader,
                "getGpsStatus", GpsStatus.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d(TAG, "getGpsStatus");
                        GpsStatus gss = (GpsStatus) param.getResult();
                        if (gss == null)
                            return;

                        Class<?> clazz = GpsStatus.class;
                        Method m = null;
                        for (Method method : clazz.getDeclaredMethods()) {
                            if (method.getName().equals("setStatus")) {
                                if (method.getParameterTypes().length > 1) {
                                    m = method;
                                    break;
                                }
                            }
                        }
                        if (m == null)
                            return;

                        //access the private setStatus function of GpsStatus
                        m.setAccessible(true);

                        //make the apps belive GPS works fine now
                        int svCount = 5;
                        int[] prns = {1, 2, 3, 4, 5};
                        float[] snrs = {0, 0, 0, 0, 0};
                        float[] elevations = {0, 0, 0, 0, 0};
                        float[] azimuths = {0, 0, 0, 0, 0};
                        int ephemerisMask = 0x1f;
                        int almanacMask = 0x1f;

                        //5 satellites are fixed
                        int usedInFixMask = 0x1f;

                        XposedHelpers.callMethod(gss, "setStatus", svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                        param.args[0] = gss;
                        param.setResult(gss);
                        try {
                            m.invoke(gss, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                            param.setResult(gss);
                        } catch (Exception e) {
                            XposedBridge.log(e);
                        }
                    }
                });

        for (Method method : LocationManager.class.getDeclaredMethods()) {
            if (method.getName().equals("requestLocationUpdates")
                    && !Modifier.isAbstract(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())) {
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d(TAG, "requestLocationUpdates");
                        if (param.args.length >= 4 && (param.args[3] instanceof LocationListener)) {

                            LocationListener ll = (LocationListener) param.args[3];

                            Class<?> clazz = LocationListener.class;
                            Method m = null;
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.getName().equals("onLocationChanged") && !Modifier.isAbstract(method.getModifiers())) {
                                    m = method;
                                    break;
                                }
                            }
                            Location l = new Location(LocationManager.GPS_PROVIDER);
                            l.setLatitude(salted(la));
                            l.setLongitude(salted(lo));
                            l.setAccuracy(10.00f);
                            l.setTime(System.currentTimeMillis());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                            }
                            XposedHelpers.callMethod(ll, "onLocationChanged", l);
                            try {
                                if (m != null) {
                                    m.invoke(ll, l);
                                }
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                        }
                    }
                });
            }

            if (method.getName().equals("requestSingleUpdate ")
                    && !Modifier.isAbstract(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())) {
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Log.d(TAG, "requestSingleUpdate");
                        if (param.args.length >= 3 && (param.args[1] instanceof LocationListener)) {

                            LocationListener ll = (LocationListener) param.args[3];

                            Class<?> clazz = LocationListener.class;
                            Method m = null;
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.getName().equals("onLocationChanged") && !Modifier.isAbstract(method.getModifiers())) {
                                    m = method;
                                    break;
                                }
                            }

                            try {
                                if (m != null) {
                                    Location l = new Location(LocationManager.GPS_PROVIDER);
                                    l.setLatitude(salted(la));
                                    l.setLongitude(salted(lo));
                                    l.setAccuracy(100f);
                                    l.setTime(System.currentTimeMillis());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                                    }
                                    m.invoke(ll, l);
                                }
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                        }
                    }
                });
            }
        }
    }

}
