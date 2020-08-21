package org.gallonyin.weworkhk;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by gallonyin on 2018/6/13.
 *
 * todo List 添加数据库
 *      开启校偏 0.002    -0.005
 *      界面左拉框
 *      腾讯获取坐标点 http://lbs.qq.com/tool/getpoint/
 *      开关
 */

public class MainActivity extends AppCompatActivity {


    private EditText et_la;
    private EditText et_lo;
    private EditText et_pic_path;
    private ImageView iv_pick_pic;
    private CheckBox cb_open;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("main", Context.MODE_PRIVATE);

        initView();
    }

    private void initView() {
        et_la = findViewById(R.id.et_la);
        et_lo = findViewById(R.id.et_lo);
        et_pic_path = findViewById(R.id.et_pic_path);
        iv_pick_pic = findViewById(R.id.iv_pick_pic);
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
        findViewById(R.id.bt_save_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String la = et_la.getText().toString();
                String lo = et_lo.getText().toString();
                if (la.equals("") || lo.equals("")) {
                    Toast.makeText(MainActivity.this, "格式异常", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent("weworkdk_gps");
                intent.putExtra("data", la + "#" + lo);
                sendBroadcast(intent);
                Toast.makeText(MainActivity.this, "保存修改成功", Toast.LENGTH_LONG).show();
                sp.edit().putFloat("la", Float.parseFloat(la))
                        .putFloat("lo", Float.parseFloat(lo))
                        .apply();
            }
        });

        findViewById(R.id.bt_pick_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotoSelectorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("limit", 1);//number是选择图片的数量
                startActivityForResult(intent, 0);
            }
        });

        findViewById(R.id.bt_save_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pic_path = et_pic_path.getText().toString();
                Intent intent = new Intent("weworkdk_pic");
                intent.putExtra("data", pic_path);
                sendBroadcast(intent);
                Toast.makeText(MainActivity.this, "保存修改成功", Toast.LENGTH_LONG).show();
                sp.edit().putString("PicPath", pic_path).apply();
            }
        });

        TextView tv_confirm = findViewById(R.id.tv_confirm);
        if (confirm()) {
            tv_confirm.setText("插件已正常启动");
        } else {
        }

        startService(new Intent(this, LongRunningService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");//path是选择拍照或者图片的地址数组
                    Log.e("paths", paths.toString());
                    if (paths.size() == 1) {
                        String value = paths.get(0);
                        File imgFile = new  File(value);
                        if (imgFile.exists()) {
                            try {
                                File newFile = new File("/storage/emulated/0/Tencent/WeixinWork/data/attendance/" + imgFile.getName());
                                Util.copy(imgFile, newFile);
                                Bitmap myBitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                                et_pic_path.setText(newFile.getAbsolutePath());
                                iv_pick_pic.setImageBitmap(myBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private boolean confirm() {
        return false;
    }
}
