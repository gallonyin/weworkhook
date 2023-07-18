package org.gallonyin.weworkhk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.UiSettings;

/**
 * 腾讯地图
 * Created by gallonyin on 2018/6/20.
 */

public class TencentMapActivity extends AppCompatActivity {
    private static final String TAG = "TencentMapActivity";

    private MapView mapView;
    private TencentMap tencentMap;
    private SharedPreferences sp;
    private float laDefault;
    private float loDefault;

    public static void enterActivity(Context context) {
        Intent intent = new Intent(context, TencentMapActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tencent_map);
        sp = getSharedPreferences("main", Context.MODE_PRIVATE);
        laDefault = sp.getFloat("la", 31.984240f);
        loDefault = sp.getFloat("lo", 118.763820f);

        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        initMap();
    }

    private void initMap() {
        //获取TencentMap实例
        tencentMap = mapView.getMap();
        //设置地图中心点
        tencentMap.setCenter(new LatLng(laDefault, loDefault));
        //设置缩放级别
        tencentMap.setZoom(11);

        //获取UiSettings实例
        UiSettings uiSettings = mapView.getUiSettings();
//        设置logo到屏幕底部中心
        uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_CENTER_BOTTOM);
        //设置比例尺到屏幕右下角
        uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_RIGHT_BOTTOM);
        //启用缩放手势(更多的手势控制请参考开发手册)
        uiSettings.setZoomGesturesEnabled(true);

        //Map点击事件
        tencentMap.setOnMapClickListener(new TencentMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showMarker(latLng.getLatitude(), latLng.getLongitude());
            }
        });

        showMarker(laDefault, loDefault);
    }

    private void showMarker(final double la, final double lo) {
        tencentMap.clearAllOverlays();

        Marker marker = tencentMap.addMarker(new MarkerOptions()
                .position(new LatLng(la, lo))
                .title(la + ", " + lo + " (点我保存)")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .defaultMarker())
                .draggable(true));
        marker.showInfoWindow();// 设置默认显示一个infoWindow

        //infoWindow点击事件
        tencentMap.setOnInfoWindowClickListener(new TencentMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                Intent intent = new Intent("weworkdk_gps");
                intent.putExtra("data", la + "#" + lo);
                sendBroadcast(intent);
                Toast.makeText(mapView.getContext(), "已更新坐标", Toast.LENGTH_SHORT).show();
                sp.edit().putFloat("la", (float) la)
                        .putFloat("lo", (float) lo)
                        .apply();
            }
        });
//        //Marker拖拽事件
//        tencentMap.setOnMarkerDraggedListener(new TencentMap.OnMarkerDraggedListener() {
//
//            //拖拽开始时调用
//            @Override
//            public void onMarkerDragStart(Marker arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            //拖拽结束后调用
//            @Override
//            public void onMarkerDragEnd(Marker arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            //拖拽时调用
//            @Override
//            public void onMarkerDrag(Marker arg0) {
//                // TODO Auto-generated method stub
//
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }
}
