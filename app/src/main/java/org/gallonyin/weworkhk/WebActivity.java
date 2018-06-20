package org.gallonyin.weworkhk;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 网页加载
 * Created by gallonyin on 2018/6/20.
 */

public class WebActivity extends AppCompatActivity {
    private static final String TAG = "WebActivity";

    private FrameLayout fl_root;
    private WebView webview;
    private String url;

    public static void enterActivity(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        url = getIntent().getStringExtra("url");

        initView();

        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showTipPop();
                    }
                });
            }
        }.start();
    }

    protected void initView() {
        fl_root = findViewById(R.id.fl_root);
        webview = findViewById(R.id.webview);

        //声明WebSettings子类
        WebSettings webSettings = webview.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webview.loadUrl(url);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }});

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webview.removeAllViews();
        webview.destroy();
        webview = null;

//        System.exit(0); // 强制关闭进程 请确保该界面另开进程
    }

    //让加载的h5页面支持回退，而不是直接退出浏览器
    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

    private void showTipPop() {
        View windowView = View.inflate(this, R.layout.popup_gps_tip, null);
        TextView mTip = windowView.findViewById(R.id.tip_text);
        mTip.setText("您获取的GPS为：");
        final PopupWindow popuWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popuWindow.setContentView(windowView);
        ColorDrawable cd = new ColorDrawable(0x000000);
        popuWindow.setBackgroundDrawable(cd);
        //产生背景变暗效果
        //        WindowManager.LayoutParams lp =getWindow().getAttributes();
        //        lp.alpha = 0.6f;
        //        getWindow().setAttributes(lp);
        popuWindow.setOutsideTouchable(true);
        popuWindow.setFocusable(false);
        popuWindow.showAsDropDown(fl_root, 0, 20);

        popuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            //在dismiss中恢复透明度
            public void onDismiss() {
            }
        });

        windowView.postDelayed(new Runnable() {
            @Override
            public void run() {
                popuWindow.dismiss();
            }
        }, 5000);

    }

}
