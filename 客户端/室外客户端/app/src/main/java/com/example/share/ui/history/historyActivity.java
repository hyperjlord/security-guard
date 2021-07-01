package com.example.share.ui.history;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.share.service.mapService.BoundaryUpdate.Boundary;
import com.example.share.service.mapService.BoundaryUpdate.Initiate;
import com.example.share.service.mapService.BoundaryUpdate.Point;
import com.example.share.service.mapService.BoundaryUpdate.Route;
import com.example.share.service.mapService.BoundaryUpdate.RouteSet;
import com.example.share.thread.netThread.getAllTrackRequest;
import com.example.share.thread.netThread.getBoundaryRequest;
import com.example.share.thread.netThread.getHistoryRequest;
import com.example.share.ui.home.MainActivity;
import com.example.share.ui.map.MapActivity;
import com.example.share.util.PointUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;

import com.example.share.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class historyActivity extends AppCompatActivity {

    MapView mMapView = null;
    AMap aMap = null;
    //划线
    Polyline polyline;
    //划范围
    Polygon polygon;
    //线程
    private Handler mHandler;
//    drawLine drawThead;
    private List<LatLng> historyList=new ArrayList<>();

    //标点
    private MarkerOptions startMarker;
    private MarkerOptions endMarker;
    private Marker marker;

    drawLineBoundary timeThead;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
//
        

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //以下开始是👴的代码

        setContentView(R.layout.activity_history);
        mMapView = (MapView) findViewById(R.id.map);
        //地图构建
        mMapView.onCreate(savedInstanceState);

//        timeThead=new drawLineBoundary();
//        HandlerThread thread = new HandlerThread("MyHandlerThread");
//        thread.start();//创建一个HandlerThread并启动它
//        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
//        mHandler.post(timeThead);//将线程post到Handler中

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void init() throws JSONException {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        //初始化起点,终点
        if(getHistoryRequest.historyArray!=null) {
            historyList=getHistoryRequest.getListByRes();
            LatLng latLng = new LatLng( historyList.get(0).latitude, historyList.get(0).longitude);
            startMarker=new MarkerOptions().position(latLng).title("起点").snippet(getHistoryRequest.getStartTime());
            marker=aMap.addMarker(startMarker);

            latLng = new LatLng( historyList.get(historyList.size()-1).latitude, historyList.get(historyList.size()-1).longitude);
            endMarker=new MarkerOptions().position(latLng).title("终点").snippet(getHistoryRequest.getEndTime());
            aMap.addMarker(endMarker);
        }

        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.isInfoWindowShown()) {
                    marker.setInfoWindowEnable(false);

                }
                else
                {
                    marker.setInfoWindowEnable(true);
                    marker.showInfoWindow();
                }
                return false;
            }
        };

        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);

        //设置amap的一些属性
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(false);// 设置指南针是否显示
        uiSettings.setZoomControlsEnabled(false);// 设置缩放按钮是否显示
        uiSettings.setScaleControlsEnabled(false);// 设置比例尺是否显示
        uiSettings.setRotateGesturesEnabled(true);// 设置地图旋转是否可用
        uiSettings.setTiltGesturesEnabled(true);// 设置地图倾斜是否可用
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        //LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER：特点：会晃
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
//
//        aMap.setMyLocationStyle(myLocationStyle);
////        aMap.setLocationSource(this);// 设置定位监听 但是现在定位相关的先关了
//
//        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 缩放级别（zoom）：值越大地图越详细(4-20)
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        aMap.setMapTextZIndex(2);


        drawLines();

    }

    public void back(View view) {
        finish();

    }

    public void drawLines() throws JSONException {


        if(historyList.size()==0)
        {
            historyList=getBoundaryRequest.getListByRes();
            // 声明 多边形参数对象
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(historyList);
            polygonOptions.strokeWidth(15) // 多边形的边框
                    .strokeColor(Color.argb(50, 1, 1, 1)) // 边框颜色
                    .fillColor(Color.argb(50, 1, 1, 1));   // 多边形的填充色
            polygon = aMap.addPolygon(polygonOptions);


            // 获取轨迹坐标点
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < historyList.size(); i++) {
                b.include(historyList.get(i));
            }


            LatLngBounds bounds = b.build();

            CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 100);//
            aMap.animateCamera(update);
        }
        else {
            Log.e("draw", String.valueOf(historyList.size()));
            PolylineOptions options = new PolylineOptions();
            options.setCustomTexture(BitmapDescriptorFactory.fromResource(1));
            options.geodesic(true).setDottedLine(false).color(Color.GREEN).addAll(historyList).useGradient(true).width(10).visible(true);
            polyline = aMap.addPolyline(options);


            // 获取轨迹坐标点
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < historyList.size(); i++) {
                b.include(historyList.get(i));
            }


            LatLngBounds bounds = b.build();

            CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 100);//
            aMap.animateCamera(update);
        }
    }
    @Override
    protected void onDestroy() {


//        mlocationClient.stopLocation();
//        mlocationClient.onDestroy();
        Log.e("关闭","结束了");
        getHistoryRequest.historyArray=null;
        getBoundaryRequest.BoundaryArray=null;
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    class drawLineBoundary implements Runnable{

        @Override
        public void run() {
                try {
                    Thread.sleep(5000);

                    Route route=getHistoryRequest.getRouteByRes();
                    RouteSet routeSet=new RouteSet();
                    routeSet.addRoute(route);
                    Initiate initiate=new Initiate(routeSet,3);

                    Log.e("运行结果",initiate.getResult().toString());
                    Boundary boundary= initiate.enclosure;
                    List<LatLng> latLngList=new ArrayList<>();
                    for(Point point:boundary.boundary)
                    {
                        LatLng latLng=new LatLng(point.latitude,point.longitude);
                        latLngList.add(latLng);
                    }
                    Log.e("draw", String.valueOf(latLngList.size()));
                    PolylineOptions options = new PolylineOptions();
                    options.setCustomTexture(BitmapDescriptorFactory.fromResource(1));
                    options.geodesic(true).setDottedLine(false).color(Color.BLUE).addAll(latLngList).useGradient(true).width(10).visible(true);
                    polyline = aMap.addPolyline(options);

                    // 获取轨迹坐标点
                    LatLngBounds.Builder b = LatLngBounds.builder();
                    for (int i = 0; i <latLngList.size(); i++) {
                        b.include(latLngList.get(i));
                    }


                    LatLngBounds bounds = b.build();

                    CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 100);//
                    aMap.animateCamera(update);


                }
                catch (JSONException | InterruptedException e) {
                    e.printStackTrace();

                }
            }

    }
}