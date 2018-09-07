package org.gallonyin.weworkhk;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.gallonyin.weworkhk.util.BitmapUtils;
import org.gallonyin.weworkhk.util.ShellUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gallonyin on 2018/6/13.
 * <p>
 * todo List 添加数据库
 * 开启校偏 0.002    -0.005
 * 界面左拉框
 * 腾讯获取坐标点 http://lbs.qq.com/tool/getpoint/
 * 开关
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int SCREENSHOT = 10;

    private EditText et_la;
    private EditText et_lo;
    private CheckBox cb_open;
    private SharedPreferences sp;

    boolean success = false;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SCREENSHOT) {
                Intent intent = new Intent("weworkdk_screenshot");
                sendBroadcast(intent);
                sendEmptyMessageDelayed(SCREENSHOT, 8000);
                startDK();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("main", Context.MODE_PRIVATE);

        initView();
    }

    public int calculateInSampleSize(BitmapFactory.Options op, int reqWidth,
                                     int reqheight) {
        int originalWidth = op.outWidth;
        int originalHeight = op.outHeight;
        int inSampleSize = 1;
        if (originalWidth > reqWidth || originalHeight > reqheight) {
            int halfWidth = originalWidth / 2;
            int halfHeight = originalHeight / 2;
            while ((halfWidth / inSampleSize > reqWidth)
                    && (halfHeight / inSampleSize > reqheight)) {
                inSampleSize *= 2;
            }
        }
        inSampleSize /= 2;
        return inSampleSize;
    }

    private void initView() {
        et_la = findViewById(R.id.et_la);
        et_lo = findViewById(R.id.et_lo);
        cb_open = findViewById(R.id.cb_open);
        cb_open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent("weworkdk_open");
                intent.putExtra("open", isChecked);
                sendBroadcast(intent);
            }
        });
        findViewById(R.id.bt_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TencentMapActivity.enterActivity(MainActivity.this);
            }
        });
        findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String la = et_la.getText().toString();
//                String lo = et_lo.getText().toString();
//                if (la.equals("") || lo.equals("")) {
//                    Toast.makeText(MainActivity.this, "格式异常", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                Intent intent = new Intent("weworkdk_gps");
//                intent.putExtra("data", la + "#" + lo);
//                sendBroadcast(intent);
//                Toast.makeText(MainActivity.this, "保存修改成功", Toast.LENGTH_LONG).show();
//                sp.edit().putFloat("la", Float.parseFloat(la))
//                        .putFloat("lo", Float.parseFloat(lo))
//                        .apply();

                //test todo

                Intent i = new Intent("weworkdk_activity");
                i.putExtra("start", true);
                sendBroadcast(i);
                success = false;
                handler.removeMessages(SCREENSHOT);
                handler.sendEmptyMessageDelayed(SCREENSHOT, 8000);
            }
        });

        startService(new Intent(this, LongRunningService.class));
    }

    public static class LongRunningService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(final Intent intent, int flags, int startId) {
            new Thread() {
                @Override
                public void run() {
                }
            }.start();
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            long triggerAtTime = System.currentTimeMillis() + 60 * 60 * 1000;
            Intent i = new Intent(this, LongRunningService.class);
            manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, PendingIntent.getService(this, 0, i, 0));
            return super.onStartCommand(intent, flags, startId);
        }
    }

    private void startDK() {
        Log.e(TAG, "startDK()");
//        startActivity(new Intent(this, MainActivity.class));
//        Intent i = new Intent(this, MainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
        Log.e(TAG, "startDK()222233");
//        new Thread() {
//            @Override
//            public void run() {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile("/sdcard/autoshot.png", options);
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                options.inSampleSize = calculateInSampleSize(options, 200, 200);
//                options.inJustDecodeBounds = false;
//                final Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/autoshot.png", options);
//                Log.e(TAG, "startDK()3");
//                if (bitmap == null) {
//                    toast("截图失败 请查看root权限");
//                    return;
//                }
//                Log.e(TAG, "startDK()4");
//
//                final int circle_color = -80537; //(254, 197, 103)
//                final int w = bitmap.getWidth();
//                final int h = bitmap.getHeight();
//                int point_count = 0;
//                int last_color = 0;
//                int[] circleBroads = new int[4];
//                Log.e(TAG, "w: " + w + " h: " + h);
//                for (int flow_h = (int) (h * 0.55); flow_h < h; flow_h++) {
//                    int pixel_color = bitmap.getPixel((w / 2), flow_h);
////                    Log.d(TAG, "flow_h: " + flow_h + " pixel: " + Color.red(pixel_color) + Color.green(pixel_color) + Color.blue(pixel_color));
//                    if (circle_color == pixel_color && circle_color != last_color) {
//                        circleBroads[point_count++] = flow_h;
//                    }
//                    if (circle_color != pixel_color && circle_color == last_color) {
//                        circleBroads[point_count++] = flow_h;
//                    }
//                    last_color = pixel_color;
//                    if (point_count == 4) break;
//                }
//                Log.e(TAG, String.format("circleBroads: %d, %d, %d, %d", circleBroads[0], circleBroads[1], circleBroads[2], circleBroads[3]));
//
//                int r = (circleBroads[2] - circleBroads[1]) / 2;
//                //圆上内缘 circleBroads[1] 圆下内缘 circleBroads[2]
//                //圆心 w / 2, (circleBroads[1] + circleBroads[2]) / 2
//                //内直径 2r = circleBroads[2] - circleBroads[1]
//                //文字识别区(下班打卡) width = w / 2 - r ~ w / 2 + r, height = circleBroads[1] + 0.55*2r ~ circleBroads[1] + 0.77*2r
//
//                //提取文字区边界
//                int left = 100000;
//                int right = 0;
//                int top = 100000;
//                int bottom = 0;
//                for (int j = (int) (circleBroads[1] + 0.55 * 2 * r); j < circleBroads[1] + 0.77 * 2 * r; j++) {
//                    for (int i = w / 2 - r; i < w / 2 + r; i++) {
//                        int pixel = bitmap.getPixel(i, j);
//                        if (Color.blue(pixel) < 103) {
////                            Log.e(TAG, "pixel: " + Color.red(pixel) + Color.green(pixel) + Color.blue(pixel));
//                            left = left > i ? i : left;
//                            right = right < i ? i : right;
//                            top = top > j ? j : top;
//                            bottom = bottom < j ? j : bottom;
//                        }
//                    }
//                }
//                Log.e(TAG, String.format("textBroads: %d, %d, %d, %d", left, right, top, bottom));
//
//                //提取文字区像素集
//                List<Integer> colors = new ArrayList<>();
//                for (int j = top; j < bottom; j++) {
//                    for (int i = left; i < right; i++) {
//                        int pixel = bitmap.getPixel(i, j);
////                        Log.e(TAG, "pixel: " + Color.red(pixel) + Color.green(pixel) + Color.blue(pixel));
//                        colors.add(pixel);
//                    }
//                }
//
//                int width = right - left;
//                int height = bottom - top;
//                int[] array = new int[colors.size()];
//                for (int i = 0; i < colors.size(); i++) {
//                    array[i] = colors.get(i);
//                }
//                if (width <= 0) {
//                    toast("图片内容提取失败");
//                    return;
//                }
//                final Bitmap bmp = Bitmap.createBitmap(array, 0, width, width, height, Bitmap.Config.ARGB_8888);
////                final Bitmap bmp2 = BitmapUtils.convertGreyImg(ThumbnailUtils.extractThumbnail(bmp, bmp.getWidth() / 2, bmp.getHeight() / 2));
//                final Bitmap bmp64 = BitmapUtils.convertGreyImg(ThumbnailUtils.extractThumbnail(bmp, 74, 16));
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        ImageView iv_test = findViewById(R.id.iv_test);
//                        iv_test.setImageBitmap(bmp64);
//                        Log.e(TAG, "ww2:" + bmp64.getWidth());
//                        Log.e(TAG, "hh" + bmp64.getHeight());
//                    }
//                });
//
//                //if ???
//                success = true;
//                handler.removeMessages(SCREENSHOT);
////                Intent i = new Intent("weworkdk_activity");
////                i.putExtra("start", false);
////                sendBroadcast(i);
//            }
//
//            private void toast(final String s) {
//                Log.e(TAG, "toast:" + s);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//        }.start();
    }
}
