package org.gallonyin.weworkhk.util;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Custom toast to show
 * Created by gallon on 2017/5/18
 */

public class CustomToast {

    private CustomToast() {
        throw new UnsupportedOperationException("u can't instantiate me ...");
    }

    private static Toast sToast;
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static Application sApplication;

    /**
     * toast initialization
     * @param application
     */
    public static void init(Application application) {
        sApplication = application;
    }

    /**
     * 安全地显示短时吐司
     *
     * @param context 上下文
     * @param text    文本
     */
    public static void showToastSafe(final Context context, final CharSequence text) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, text, Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 安全地显示短时吐司
     *
     * @param context 上下文
     * @param resId   资源Id
     */
    public static void showToastSafe(final Context context, final int resId) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, resId, Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 安全地显示短时吐司
     *
     * @param context 上下文
     * @param resId   资源Id
     * @param args    参数
     */
    public static void showToastSafe(final Context context, final int resId, final Object... args) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, resId, Toast.LENGTH_SHORT, args);
            }
        });
    }

    /**
     * 安全地显示短时吐司
     *
     * @param context 上下文
     * @param format  格式
     * @param args    参数
     */
    public static void showToastSafe(final Context context, final String format, final Object... args) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, format, Toast.LENGTH_SHORT, args);
            }
        });
    }

    /**
     * 安全地显示长时吐司
     *
     * @param context 上下文
     * @param text    文本
     */
    public static void showLongToastSafe(final Context context, final CharSequence text) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, text, Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 安全地显示长时吐司
     *
     * @param context 上下文
     * @param resId   资源Id
     */
    public static void showLongToastSafe(final Context context, final int resId) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, resId, Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 安全地显示长时吐司
     *
     * @param context 上下文
     * @param resId   资源Id
     * @param args    参数
     */
    public static void showLongToastSafe(final Context context, final int resId, final Object... args) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, resId, Toast.LENGTH_LONG, args);
            }
        });
    }

    /**
     * 安全地显示长时吐司
     *
     * @param context 上下文
     * @param format  格式
     * @param args    参数
     */
    public static void showLongToastSafe(final Context context, final String format, final Object... args) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(context, format, Toast.LENGTH_LONG, args);
            }
        });
    }

    /**
     * 显示短时吐司
     *
     * @param context 上下文
     * @param text    文本
     */
    public static void showToast(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短时吐司
     *
     * @param context 上下文
     * @param resId   资源Id
     */
    public static void showToast(Context context, int resId) {
        showToast(context, resId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短时吐司
     *
     * @param context 上下文
     * @param resId   资源Id
     * @param args    参数
     */
    public static void showToast(Context context, int resId, Object... args) {
        showToast(context, resId, Toast.LENGTH_SHORT, args);
    }

    /**
     * 显示短时吐司
     *
     * @param context 上下文
     * @param format  格式
     * @param args    参数
     */
    public static void showToast(Context context, String format, Object... args) {
        showToast(context, format, Toast.LENGTH_SHORT, args);
    }

    /**
     * 显示长时吐司
     *
     * @param context 上下文
     * @param text    文本
     */
    public static void showLongToast(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时吐司
     *
     * @param text    文本
     */
    public static void showLongToast(CharSequence text) {
        showToast(sApplication, text, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时吐司
     *
     * @param context 上下文
     * @param resId   资源Id
     */
    public static void showLongToast(Context context, int resId) {
        showToast(context, resId, Toast.LENGTH_LONG);
    }

    /**
     * 显示长时吐司
     *
     * @param context 上下文
     * @param resId   资源Id
     * @param args    参数
     */
    public static void showLongToast(Context context, int resId, Object... args) {
        showToast(context, resId, Toast.LENGTH_LONG, args);
    }

    /**
     * 显示长时吐司
     *
     * @param context 上下文
     * @param format  格式
     * @param args    参数
     */
    public static void showLongToast(Context context, String format, Object... args) {
        showToast(context, format, Toast.LENGTH_LONG, args);
    }

    /**
     * 显示吐司
     *
     * @param context  上下文
     * @param text     文本
     * @param duration 显示时长
     */
    private synchronized static void showToast(Context context, CharSequence text, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), text, duration);
        } else {
            sToast.show();
            sToast.setText(text);
            sToast.setDuration(duration);
        }
        Log.e("MainActivity", "duration: " + duration);
        sToast.show();
    }

    /**
     * 显示吐司
     *
     * @param context  上下文
     * @param resId    资源Id
     * @param duration 显示时长
     */
    private static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getResources().getText(resId).toString(), duration);
    }

    /**
     * 显示吐司
     *
     * @param context  上下文
     * @param resId    资源Id
     * @param duration 显示时长
     * @param args     参数
     */
    private static void showToast(Context context, int resId, int duration, Object... args) {
        showToast(context, String.format(context.getResources().getString(resId), args), duration);
    }

    /**
     * 显示吐司
     *
     * @param context  上下文
     * @param format   格式
     * @param duration 显示时长
     * @param args     参数
     */
    private static void showToast(Context context, String format, int duration, Object... args) {
        showToast(context, String.format(format, args), duration);
    }

    /**
     * 取消吐司显示
     */
    public static void cancelToast() {
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }


}
