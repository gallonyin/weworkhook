package org.gallonyin.weworkhk.util;

import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.gallonyin.weworkhk.Global;
import org.gallonyin.weworkhk.Main;
import org.gallonyin.weworkhk.MainActivity;
import org.gallonyin.weworkhk.MyApplication;
import org.gallonyin.weworkhk.WeWorkClazz;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gallon2 on 2018/6/30.
 */

public class SignUtils {
    private static final String TAG = "SignUtils";

    private Context context;

    private boolean signing = false; //防止重复进入打卡流程

    private SharedPreferences sp;

    private Handler handler = new Handler(Looper.getMainLooper());

    public SignUtils(Application context, SharedPreferences sp) {
        this.context = context;
        CustomToast.init(context);
        this.sp = sp;
    }

    /**
     * @param status 1.记录上班 2.记录下班 3.上班打卡 4.下班打卡
     */
    public void sign(final int status) {
        Log.e(TAG, "signing: " + signing + " status: " + status);
        if (signing) return;
        signing = true;
        new Thread() {
            @Override
            public void run() {
                final KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                final Class<?> ParamClass = WeWorkClazz.getParamClass();
                Parcelable o = null;
                try {
                    o = (Parcelable) ParamClass.newInstance();
//                        ParamClass.getField("dMq").set(o, true);
//                        ParamClass.getField("dMr").set(o, true);
                    ParamClass.getField("from").set(o, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context, WeWorkClazz.getAttendanceActivity2Class());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("data", o);
                context.startActivity(intent);

                try {
                    int retry = 0;
                    while (!ShellUtils.execCmd("dumpsys activity top | grep ACTIVITY", true).successMsg.contains("AttendanceActivity")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "正在进入打开页", Toast.LENGTH_SHORT).show();
                            }
                        });
                        sleep(3000);
                        if (retry++ == 3) context.startActivity(intent);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "正在截图", Toast.LENGTH_SHORT).show();
                        }
                    });

                    boolean isScreenOn = pm.isInteractive();
                    if (!isScreenOn) { //click power
                        ShellUtils.execCmd("input keyevent 26", true);
                    }
                    retry = 0;
                    boolean lock;
                    while (lock = mKeyguardManager.inKeyguardRestrictedInputMode()) { //swap unlock
                        ShellUtils.execCmd("input swipe 300 800 300 300", true);
                        sleep(1000);
                        if (retry++ == 3) break;
                    }
                    if (lock) { //解锁失败 通知...
                        Log.e(TAG, "WTF");
                    } else { //打卡 息屏

                        //check & click
                        check();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void check() {
                Log.e(TAG, "check");
                String picPath = "/sdcard/autoshot3.png";
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picPath, options);
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = BitmapUtils.calculateInSampleSize(options, 200, 200);
                options.inJustDecodeBounds = false;
                final Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
                if (bitmap == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            CustomToast.showLongToast("截图失败 请查看权限");
                        }
                    });
                    Log.e(TAG, "截图失败 请查看权限");
                    goBack();
                    return;
                }

                final int circle_color = -80537; //(254, 197, 103)
                final int w = bitmap.getWidth();
                final int h = bitmap.getHeight();
                int point_count = 0;
                int last_color = 0;
                int[] circleBroads = new int[4];
                Log.e(TAG, "w: " + w + " h: " + h);
                for (int flow_h = (int) (h * 0.55); flow_h < h; flow_h++) {
                    int pixel_color = bitmap.getPixel((w / 2), flow_h);
                    if (circle_color == pixel_color && circle_color != last_color) {
                        circleBroads[point_count++] = flow_h;
                    }
                    if (circle_color != pixel_color && circle_color == last_color) {
                        circleBroads[point_count++] = flow_h;
                    }
                    last_color = pixel_color;
                    if (point_count == 4) break;
                }
                Log.e(TAG, String.format("circleBroads: %d, %d, %d, %d", circleBroads[0], circleBroads[1], circleBroads[2], circleBroads[3]));

                int r = (circleBroads[2] - circleBroads[1]) / 2;
                //圆上内缘 circleBroads[1] 圆下内缘 circleBroads[2]
                //圆心 w / 2, (circleBroads[1] + circleBroads[2]) / 2
                //内直径 2r = circleBroads[2] - circleBroads[1]
                //文字识别区(下班打卡) width = w / 2 - r ~ w / 2 + r, height = circleBroads[1] + 0.55*2r ~ circleBroads[1] + 0.77*2r

                //提取文字区边界
                int left = 100000;
                int right = 0;
                int top = 100000;
                int bottom = 0;
                for (int j = (int) (circleBroads[1] + 0.55 * 2 * r); j < circleBroads[1] + 0.77 * 2 * r; j++) {
                    for (int i = w / 2 - r; i < w / 2 + r; i++) {
                        int pixel = bitmap.getPixel(i, j);
                        if (Color.blue(pixel) < 103) {
//                            Log.e(TAG, "pixel: " + Color.red(pixel) + Color.green(pixel) + Color.blue(pixel));
                            left = left > i ? i : left;
                            right = right < i ? i : right;
                            top = top > j ? j : top;
                            bottom = bottom < j ? j : bottom;
                        }
                    }
                }

                //文字区域中心 (width=0.7*2r height=0.2*2r)
                int midX = (left + right) / 2;
                int midY = (top + bottom) / 2;
                left = (int) (midX - 0.7 * r);
                right = (int) (midX + 0.7 * r);
                top = (int) (midY - 0.2 * r);
                bottom = (int) (midY + 0.2 * r);
                Log.e(TAG, String.format("textBroads: %d, %d, %d, %d", left, right, top, bottom));

                //提取文字区像素集
                List<Integer> colors = new ArrayList<>();
                for (int j = top; j < bottom; j++) {
                    for (int i = left; i < right; i++) {
                        int pixel = bitmap.getPixel(i, j);
//                                  Log.e(TAG, "pixel: " + Color.red(pixel) + Color.green(pixel) + Color.blue(pixel));
                        colors.add(pixel);
                    }
                }

                int width = right - left;
                int height = bottom - top;
                int[] array = new int[colors.size()];
                for (int i = 0; i < colors.size(); i++) {
                    array[i] = colors.get(i);
                }
                if (width <= 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            CustomToast.showLongToast("图片内容提取失败");
                        }
                    });
                    goBack();
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        CustomToast.showLongToast("图片指纹获取成功");
                    }
                });
                final Bitmap bmp = Bitmap.createBitmap(array, 0, width, width, height, Bitmap.Config.RGB_565);
                final Bitmap bmp64 = BitmapUtils.convertGreyImg(ThumbnailUtils.extractThumbnail(bmp, bmp.getWidth() / 4, bmp.getHeight() / 4));
                int avg = BitmapUtils.getAvg(bmp64);
                String binary = BitmapUtils.getBinary(bmp64, avg);
                binary = binary.substring(0, binary.length() - binary.length() % 8);

                Log.e(TAG, "avg: " + avg);
                Log.e(TAG, "binary: " + binary);
                String new_id = BitmapUtils.binaryString2hexString(binary);

                Log.e(TAG, "图片指纹: " + new_id);

                if (status == Global.RECORD_GO_TO_WORK || status == Global.RECORD_GET_OFF_WORK) {
                    sp.edit().putString(status + "", new_id).apply();
                    goBack();
                } else {
                    String id = sp.getString(status - 2 + "", "");
                    if (BitmapUtils.isTheTime(id, new_id)) {
                        int tapX = (int) (midX + Math.random() * 20 - 10);
                        int tapY = (int) (midY + Math.random() * 20 - 10);
                        ShellUtils.execCmd("input tap " + tapX + " " + tapY, true);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                CustomToast.showLongToast("打卡");
                            }
                        });
                        try {
                            sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //...loop
                        Toast.makeText(context, "打卡失败", Toast.LENGTH_SHORT).show();
                    }
                    goBack();
                }
            }

            private void goBack() {
                signing = false;
                ShellUtils.execCmd("input keyevent 4", true);
                ShellUtils.execCmd("input keyevent 26", true);
                ShellUtils.execCmd("am start -n org.gallonyin.weworkhk/.MainActivity", true);
            }
        }.start();
    }

}