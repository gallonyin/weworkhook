package org.caworks.wechathk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by gallonyin on 2018/6/13.
 */

public class MainActivity extends AppCompatActivity {

    private EditText et_la;
    private EditText et_lo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        et_la = findViewById(R.id.et_la);
        et_lo = findViewById(R.id.et_lo);
        findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String la = et_la.getText().toString();
                String lo = et_lo.getText().toString();
                if (la.equals("") || lo.equals("")) {
                    Toast.makeText(MainActivity.this, "格式异常", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent("weworkdk");
                intent.putExtra("data", la + "#" + lo);
                sendBroadcast(intent);
                Toast.makeText(MainActivity.this, "保存修改成功", Toast.LENGTH_LONG).show();
            }
        });
    }
}
