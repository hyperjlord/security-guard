package com.example.share.ui.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.share.R;
import com.example.share.service.mapService.BoundaryUpdate.*;
import com.example.share.entity.boundarySet;
import com.example.share.ui.history.historyActivity;
import com.example.share.ui.home.MainActivity;
import com.example.share.ui.map.datepicker.CustomDatePicker;
import com.example.share.ui.map.datepicker.DateFormatUtils;
import com.example.share.util.PointUtils;
import com.example.share.thread.netThread.*;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MapActivity extends Activity implements LocationSource, AMapLocationListener {
    //地图view
    MapView mMapView = null;
    AMap aMap = null;

    List<LatLng> points = new ArrayList<LatLng>();
    List<LatLng> sendPoints=new ArrayList<>();
    //划线
    Polyline polyline;
    LatLng lastLatLng=new LatLng(0,0);
    //线程池
    ExecutorService pool = Executors.newFixedThreadPool(5);
    private PowerManager.WakeLock wakeLock;
    //定位
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    //线程
    private Handler mHandler;
    drawLine timeThead;


    //标点
    private  MarkerOptions startMarker;
    private MarkerOptions endMarker;
    private Marker marker;

    //时间选择器
    private TextView mTvSelectedDate;
    private CustomDatePicker mDatePicker;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //开启后台运行
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.ON_AFTER_RELEASE
                | PowerManager.PARTIAL_WAKE_LOCK, "Tag");
        //申请WakeLock
        wakeLock.acquire(10*60*1000L /*10 minutes*/);


        Window window = getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );//可使其保持常亮 以保证我们的算法运行


        //到这 Destroy时记得注销
        //创建三步
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.map);
        //地图构建
        mMapView.onCreate(savedInstanceState);
        lastLatLng=MainActivity.points.get(0);

        getAllTrackRequest.getRes=false;//这个变量在这个版本已弃用
        init();
        mTvSelectedDate=findViewById(R.id.dateText);
        initDatePicker();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {

        //Log.e("map","暂停");
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        wakeLock.release();
        pool.shutdown();
//        mlocationClient.stopLocation();
//        mlocationClient.onDestroy();
        mMapView.onDestroy();
        mDatePicker.onDestroy();
        mMapView = null;
        mLocationOption=null;
        getAllTrackRequest.getRes=false;
        mHandler.removeCallbacks(timeThead);
        super.onDestroy();
    }

    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        //初始化起点
        if(MainActivity.points.size()>0) {
            LatLng latLng = new LatLng(MainActivity.points.get(0).latitude,MainActivity.points.get(0).longitude);
            Calendar cal = Calendar.getInstance();
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH)+1;
            int d = cal.get(Calendar.DATE);
            int h = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            startMarker=new MarkerOptions().position(latLng).title("起点").snippet("时间:"+y+"-"+m+"-"+d+" "+h+":"+min);
            marker=aMap.addMarker(startMarker);
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
        uiSettings.setCompassEnabled(true);// 设置指南针是否显示
        uiSettings.setZoomControlsEnabled(true);// 设置缩放按钮是否显示
        uiSettings.setScaleControlsEnabled(true);// 设置比例尺是否显示
        uiSettings.setRotateGesturesEnabled(true);// 设置地图旋转是否可用
        uiSettings.setTiltGesturesEnabled(true);// 设置地图倾斜是否可用
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER：特点：会晃
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。

        aMap.setMyLocationStyle(myLocationStyle);
//        aMap.setLocationSource(this);// 设置定位监听 但是现在定位相关的先关了

        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 缩放级别（zoom）：值越大地图越详细(4-20)
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        aMap.setMapTextZIndex(2);



        timeThead=new drawLine();
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
         mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        mHandler.post(timeThead);//将线程post到Handler中

    }

    /**
     * 定位成功后回调函数
     * 已停用
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        while(!getAllTrackRequest.getRes)
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            points.addAll(PointUtils.getListByRes());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("map","我在");
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                LatLng newLatLng = new LatLng((double) Math.round(amapLocation.getLatitude()*1000000)/1000000,(double) Math.round( amapLocation.getLongitude()*1000000)/1000000);
                Log.e("Amap", "纬度:"+newLatLng.latitude + ",经度:" + newLatLng.longitude);
                long timeStamp=System.currentTimeMillis()/1000;
               // Toast.makeText(getApplicationContext(),"已添加",Toast.LENGTH_SHORT).show();
                if (lastLatLng.latitude!=0) {
                    LatLng oldLatLng = lastLatLng;
                    //前一个点相同则将其移除
                    if(oldLatLng.latitude==newLatLng.latitude&&oldLatLng.longitude==newLatLng.longitude)
                    {
                        return;
                    }
                    //sendPoints.remove(points.size() - 1);
                    points.add(newLatLng);
                    //sendPoints.add(newLatLng);

                   patchLine(oldLatLng, newLatLng);
                } else {
                    points.add(newLatLng);
                    sendPoints.add(newLatLng);
                }
                //drawLines();
                //尝试发送
                try {
                    justiceAndSend(timeStamp);
                    //Thread.sleep(5000);
//                    Log.e("在此之前","在此之前");
                } catch ( JSONException e) {
                    e.printStackTrace();
                }
                /**
                 * 1.取id
                 * 2.读时间 timeStamp
                 * 3.判定其是否在界上
                 * 4.发送
                 */
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 绘制路线
     * 现在用的是全局的points
     */
    public void drawLines() throws JSONException {

        Log.e("draw", String.valueOf(MainActivity.points.size()));
        if(MainActivity.points.size()>0) {
            if(lastLatLng.latitude!=MainActivity.points.get(0).latitude&&lastLatLng.longitude!=MainActivity.points.get(0).longitude)
            {
//                Log.e("map","map时间点更改");
                lastLatLng=MainActivity.points.get(0);
                marker.setPosition(MainActivity.points.get(0));
                marker.setSnippet("时间："+PointUtils.getStartTime());
//                Log.e("map","时间点"+PointUtils.getStartTime());
            }
        }
        PolylineOptions options = new PolylineOptions();
        options.setCustomTexture(BitmapDescriptorFactory.fromResource(1));
        options.geodesic(true).setDottedLine(false).color(Color.GREEN).addAll(MainActivity.points).useGradient(true).width(10).visible(true);
        polyline = aMap.addPolyline(options);


        // 获取轨迹坐标点
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < MainActivity.points.size(); i++) {
            b.include(MainActivity.points.get(i));
        }


//        lastLatLng=MainActivity.points.get(MainActivity.points.size()-1);
        LatLngBounds bounds = b.build();

        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 100);//
        aMap.animateCamera(update);

    }

    /**
     * 激活定位
     * 已停用
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setOnceLocation(false);
            mLocationOption.setGpsFirst(true);
            // 设置发送定位请求的时间间隔,最小值为1000ms,1秒更新一次定位信息
            mLocationOption.setInterval(2000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationOption.setMockEnable(true);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {

    }

    /**
     * 判定且发送
     * 已停用
     */
    private void justiceAndSend(long time) throws JSONException {
        //ToDo boundarySetList现在缺失
        if(boundarySet.boundarySetList.size()>0)
        {
            for(LatLng latLng:sendPoints)
            {
                boolean isBound=false;
                for(boundarySet boundary:boundarySet.boundarySetList)
                {
                    int boundaryLength=boundary.boundary_set_json.length();
                   for(int i=0;i<boundaryLength;i++)
                   {
                       JSONObject boundaryJson=boundary.boundary_set_json.getJSONObject(i);
                       if(latLng.longitude==(double)boundaryJson.get("longitude")&&latLng.latitude==(double)boundaryJson.get("latitude"))
                       {
                           isBound=true;
                           PointUtils.trackAddPoint(latLng,true,time);
                           //ToDo alert
                       }
                   }
                }
                if(!isBound)
                {
                    PointUtils.trackAddPoint(latLng,false,time);
                }
            }
        }
        else
        {

            PointUtils.trackAddPoints(sendPoints,false,time);
//            List<boundarySet> boundarySetList=new ArrayList<>();
//            while(true)
//            {
//                String URL="http://light.qingxu.website:25080"+"备份URL";
//                String Res=httpUtils.sendGetRequest(URL);
//                JSONObject Boundary=new JSONObject(Res);

//                //ToDo boundary JSONObject还原为boundarySet
//            }
        }
        sendPoints.clear();
        //ToDo 取account_id现在暂定为0
//        JSONObject sendObject=PointUtils.trackSetToJson(0);
//        String url="http://light.qingxu.website:25080/protect/storage";
////                "stu-project-test.qingxu.website:10008/PROTECT-PROVIDER/protect/storage";
//        PostRequest postRequest=new PostRequest(url,sendObject);
//        //线程池启动
//        pool.execute(postRequest);
    }

    /**
     * 进入系统便开始获取当前时间附近的track
     *  暂时弃用
     * @param time
     */
    private void getTrackStart(long time) {
        String url=//"http://light.qingxu.website:25080/protect/getAllTrackRecently";
                  "http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getAllTrackRecently";
        getAllTrackRequest getAllTrackRequest=new getAllTrackRequest(url, MapActivity.this);
        getAllTrackRequest.finishUrl(MainActivity.userId,time);
        pool.execute(getAllTrackRequest);
    }

    /**
     * 进入系统便开始获取当前时间附近的track
     * 暂时弃用
     * @param time
     */
    private void getTrackBySecond(long time) {
        String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getTrackBySeconds";
        getTrackRequest getTrackRequest=new getTrackRequest(url);
        getTrackRequest.finishUrl((int) MainActivity.userId,time);
        pool.execute(getTrackRequest);
    }

    /**
     * 获取当前时间2h附近的track
     * @param time
     */
    private void getTrack2h(long time) {
        String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getTrackAround2h";
        //"http://stu-project-test.qingxu.website:10008/PROTECT-PROVIDER/protect/getTrackAround2h";
        getHistoryRequest getAllTrackRequest=new getHistoryRequest(url, MapActivity.this);
        getAllTrackRequest.finishUrl(MainActivity.userId,time);
        pool.execute(getAllTrackRequest);
    }

    /**
     * 获取当前时间1day附近的track
     * @param time
     */
    private void getTrack1d(long time) {
        String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getTrackAroundDay";
        //"http://stu-project-test.qingxu.website:10008/PROTECT-PROVIDER/protect/getTrackAround2h";
        getHistoryRequest getAllTrackRequest=new getHistoryRequest(url, MapActivity.this);
        getAllTrackRequest.finishUrl(MainActivity.userId,time);
        pool.execute(getAllTrackRequest);
    }
    /**
     * 获取boundary
     */
    private void getBoundary() {
        String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getBoundaryById";
        //"http://stu-project-test.qingxu.website:10008/PROTECT-PROVIDER/protect/getTrackAround2h";
        getBoundaryRequest getBoundaryRequest=new getBoundaryRequest(url, MapActivity.this);
        getBoundaryRequest.finishUrl(MainActivity.userId);
        pool.execute(getBoundaryRequest);
    }

    /**
     * 连线补点
     *
     * @param old_point
     * @param new_point
     */
    public void patchLine(LatLng old_point, LatLng new_point) {
        LatLng from = new LatLng(0,0);
        LatLng to = new LatLng(0,0);
        double x, y, dx, dy, k, it;
        dx = Operation.sub(new_point.longitude, old_point.longitude);
        dy = Operation.sub(new_point.latitude, old_point.latitude);
        System.out.println(dx+" "+dy);
        if (dx > 0) {
            from = old_point;
            to = new_point;
        } else {
            from = new_point;
            to = old_point;
        }
        if (dx != 0) {
            k = dy / dx;
            if (Math.abs(k) <= 1) {
                y = from.latitude;
                for (x = from.longitude; x <= to.longitude; x = Operation.sum(x, 0.000001)) {
                    LatLng point = new LatLng( (double) Math.round(y * 1000000) / 1000000,x);
                    sendPoints.add(point);
                    y = Operation.sum(y, k * 0.000001);
                }
            } else {
                System.out.println("2");
                k = 1 / k;
                if (dy > 0) {
                    from = old_point;
                    to = new_point;
                } else {
                    from = new_point;
                    to = old_point;
                }
                x = from.longitude;
                for (y = from.latitude; y <= to.latitude; y = Operation.sum(y, 0.000001)) {
                    LatLng point = new LatLng(y,(double) Math.round(x * 1000000) / 1000000);
                    sendPoints.add(point);
                    x = Operation.sum(x, k * 0.000001);
                }
            }
        } else {
            if (dy > 0) {
                from = old_point;
                to = new_point;
            } else {
                from = new_point;
                to = old_point;
            }
            x = from.longitude;
            for (y = from.latitude; y <= to.latitude; y = Operation.sum(y, 0.000001)) {
                LatLng point = new LatLng( y,x);
                sendPoints.add(point);
            }
        }
    }


    public void showBoundary(View view)
    {
        Toast.makeText(this, "该功能还在测试中~", Toast.LENGTH_SHORT).show();

        getBoundary();
        while (getBoundaryRequest.BoundaryArray==null)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        goToHistoryPage();

    }

    public void showHistory(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择查询条件")
                .setItems(R.array.date_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:{

                                long time=System.currentTimeMillis()/1000;
                                getTrack2h(time);
                                while (getHistoryRequest.historyArray==null)
                                {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                goToHistoryPage();
                                //timeThead.drawSwap();暂时不需要这个了
                                //更改button信息
                                //
                                break;
                            }
                            case 1:{

                                long time=System.currentTimeMillis()/1000;
                                getTrack1d(time);
                                while (getHistoryRequest.historyArray==null)
                                {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                goToHistoryPage();
                                break;
                            }
                            case 2:{
                                //TODO: 选择指定日期的轨迹
                                mDatePicker.show(mTvSelectedDate.getText().toString());
                                break;
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_alert_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.show();

    }

    private void initDatePicker() {
        long beginTimestamp = DateFormatUtils.str2Long("2021-1-1", false);
        long endTimestamp = System.currentTimeMillis();

        mTvSelectedDate.setText(DateFormatUtils.long2Str(endTimestamp, false));

        // 通过时间戳初始化日期，毫秒级别
        mDatePicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                mTvSelectedDate.setText(DateFormatUtils.long2Str(timestamp, false));
                //开启线程
                //ToDo 开启线程获取
                long time=timestamp/1000+86400;
                getTrack1d(time);
                while (getHistoryRequest.historyArray==null)
                {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                goToHistoryPage();
                Log.e("time",timestamp+"");
            }
        }, beginTimestamp, endTimestamp);
        // 不允许点击屏幕或物理返回键关闭
        mDatePicker.setCancelable(true);
        // 不显示时和分
        mDatePicker.setCanShowPreciseTime(false);
        // 不允许循环滚动
        mDatePicker.setScrollLoop(true);
        // 不允许滚动动画
        mDatePicker.setCanShowAnim(true);
    }

    public void goToHomePage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToHistoryPage() {
        Intent intent = new Intent(this, historyActivity.class);
        startActivity(intent);
    }


    class drawLine implements Runnable{

        @Override
        public void run() {
            while (true) {
                try {

                        drawLines();
                        Thread.sleep(2000);

                }
                catch (JSONException | InterruptedException e) {
                    e.printStackTrace();

                }
            }
        }
    }
}


