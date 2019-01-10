package org.gallonyin.weworkhk;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        TextView tv_confirm = findViewById(R.id.tv_confirm);
        if (confirm()) {
            tv_confirm.setText("插件已正常启动");
        } else {
        }

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

    private boolean confirm() {
        return false;
    }
}
