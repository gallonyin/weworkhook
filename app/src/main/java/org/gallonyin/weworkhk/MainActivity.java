package org.gallonyin.weworkhk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
                TencentMapActivity.enterActivity(MainActivity.this, "http://lbs.qq.com/tool/getpoint/index.html");
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
            }
        });
    }
}
