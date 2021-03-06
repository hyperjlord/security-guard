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
    //??????view
    MapView mMapView = null;
    AMap aMap = null;

    List<LatLng> points = new ArrayList<LatLng>();
    List<LatLng> sendPoints=new ArrayList<>();
    //??????
    Polyline polyline;
    LatLng lastLatLng=new LatLng(0,0);
    //?????????
    ExecutorService pool = Executors.newFixedThreadPool(5);
    private PowerManager.WakeLock wakeLock;
    //??????
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    //??????
    private Handler mHandler;
    drawLine timeThead;


    //??????
    private  MarkerOptions startMarker;
    private MarkerOptions endMarker;
    private Marker marker;

    //???????????????
    private TextView mTvSelectedDate;
    private CustomDatePicker mDatePicker;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //??????????????????
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.ON_AFTER_RELEASE
                | PowerManager.PARTIAL_WAKE_LOCK, "Tag");
        //??????WakeLock
        wakeLock.acquire(10*60*1000L /*10 minutes*/);


        Window window = getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );//????????????????????? ??????????????????????????????


        //?????? Destroy???????????????
        //????????????
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.map);
        //????????????
        mMapView.onCreate(savedInstanceState);
        lastLatLng=MainActivity.points.get(0);

        getAllTrackRequest.getRes=false;//????????????????????????????????????
        init();
        mTvSelectedDate=findViewById(R.id.dateText);
        initDatePicker();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {

        //Log.e("map","??????");
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

        //???????????????
        if(MainActivity.points.size()>0) {
            LatLng latLng = new LatLng(MainActivity.points.get(0).latitude,MainActivity.points.get(0).longitude);
            Calendar cal = Calendar.getInstance();
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH)+1;
            int d = cal.get(Calendar.DATE);
            int h = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            startMarker=new MarkerOptions().position(latLng).title("??????").snippet("??????:"+y+"-"+m+"-"+d+" "+h+":"+min);
            marker=aMap.addMarker(startMarker);
        }

        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker ?????????????????????????????????
            // ?????? true ?????????????????????????????????????????????false
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
        // ?????? Marker ???????????????
        aMap.setOnMarkerClickListener(markerClickListener);

        //??????amap???????????????
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(true);// ???????????????????????????
        uiSettings.setZoomControlsEnabled(true);// ??????????????????????????????
        uiSettings.setScaleControlsEnabled(true);// ???????????????????????????
        uiSettings.setRotateGesturesEnabled(true);// ??????????????????????????????
        uiSettings.setTiltGesturesEnabled(true);// ??????????????????????????????
        uiSettings.setMyLocationButtonEnabled(false);// ????????????????????????????????????

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER??????????????????
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????

        aMap.setMyLocationStyle(myLocationStyle);
//        aMap.setLocationSource(this);// ?????????????????? ????????????????????????????????????

        aMap.setMyLocationEnabled(true);// ?????????true??????????????????????????????????????????false??????????????????????????????????????????????????????false
        // ???????????????zoom??????????????????????????????(4-20)
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //?????? aMap.setMapTextZIndex(2) ????????????????????????????????????????????????????????????
        aMap.setMapTextZIndex(2);



        timeThead=new drawLine();
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//????????????HandlerThread????????????
         mHandler = new Handler(thread.getLooper());//??????HandlerThread???looper????????????Handler?????????????????????????????????????????????????????????UI??????
        mHandler.post(timeThead);//?????????post???Handler???

    }

    /**
     * ???????????????????????????
     * ?????????
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
        Log.e("map","??????");
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// ?????????????????????
                LatLng newLatLng = new LatLng((double) Math.round(amapLocation.getLatitude()*1000000)/1000000,(double) Math.round( amapLocation.getLongitude()*1000000)/1000000);
                Log.e("Amap", "??????:"+newLatLng.latitude + ",??????:" + newLatLng.longitude);
                long timeStamp=System.currentTimeMillis()/1000;
               // Toast.makeText(getApplicationContext(),"?????????",Toast.LENGTH_SHORT).show();
                if (lastLatLng.latitude!=0) {
                    LatLng oldLatLng = lastLatLng;
                    //?????????????????????????????????
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
                //????????????
                try {
                    justiceAndSend(timeStamp);
                    //Thread.sleep(5000);
//                    Log.e("????????????","????????????");
                } catch ( JSONException e) {
                    e.printStackTrace();
                }
                /**
                 * 1.???id
                 * 2.????????? timeStamp
                 * 3.????????????????????????
                 * 4.??????
                 */
            } else {
                String errText = "????????????," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * ????????????
     * ????????????????????????points
     */
    public void drawLines() throws JSONException {

        Log.e("draw", String.valueOf(MainActivity.points.size()));
        if(MainActivity.points.size()>0) {
            if(lastLatLng.latitude!=MainActivity.points.get(0).latitude&&lastLatLng.longitude!=MainActivity.points.get(0).longitude)
            {
//                Log.e("map","map???????????????");
                lastLatLng=MainActivity.points.get(0);
                marker.setPosition(MainActivity.points.get(0));
                marker.setSnippet("?????????"+PointUtils.getStartTime());
//                Log.e("map","?????????"+PointUtils.getStartTime());
            }
        }
        PolylineOptions options = new PolylineOptions();
        options.setCustomTexture(BitmapDescriptorFactory.fromResource(1));
        options.geodesic(true).setDottedLine(false).color(Color.GREEN).addAll(MainActivity.points).useGradient(true).width(10).visible(true);
        polyline = aMap.addPolyline(options);


        // ?????????????????????
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
     * ????????????
     * ?????????
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //??????????????????
            mlocationClient.setLocationListener(this);
            //??????????????????????????????
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //??????????????????
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setOnceLocation(false);
            mLocationOption.setGpsFirst(true);
            // ???????????????????????????????????????,????????????1000ms,1???????????????????????????
            mLocationOption.setInterval(2000);
            // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
            // ??????????????????????????????????????????????????????????????????2000ms?????????????????????????????????stopLocation()???????????????????????????
            // ???????????????????????????????????????????????????onDestroy()??????
            // ?????????????????????????????????????????????????????????????????????stopLocation()???????????????????????????sdk???????????????
            mLocationOption.setMockEnable(true);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {

    }

    /**
     * ???????????????
     * ?????????
     */
    private void justiceAndSend(long time) throws JSONException {
        //ToDo boundarySetList????????????
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
//                String URL="http://light.qingxu.website:25080"+"??????URL";
//                String Res=httpUtils.sendGetRequest(URL);
//                JSONObject Boundary=new JSONObject(Res);

//                //ToDo boundary JSONObject?????????boundarySet
//            }
        }
        sendPoints.clear();
        //ToDo ???account_id???????????????0
//        JSONObject sendObject=PointUtils.trackSetToJson(0);
//        String url="http://light.qingxu.website:25080/protect/storage";
////                "stu-project-test.qingxu.website:10008/PROTECT-PROVIDER/protect/storage";
//        PostRequest postRequest=new PostRequest(url,sendObject);
//        //???????????????
//        pool.execute(postRequest);
    }

    /**
     * ????????????????????????????????????????????????track
     *  ????????????
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
     * ????????????????????????????????????????????????track
     * ????????????
     * @param time
     */
    private void getTrackBySecond(long time) {
        String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getTrackBySeconds";
        getTrackRequest getTrackRequest=new getTrackRequest(url);
        getTrackRequest.finishUrl((int) MainActivity.userId,time);
        pool.execute(getTrackRequest);
    }

    /**
     * ??????????????????2h?????????track
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
     * ??????????????????1day?????????track
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
     * ??????boundary
     */
    private void getBoundary() {
        String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getBoundaryById";
        //"http://stu-project-test.qingxu.website:10008/PROTECT-PROVIDER/protect/getTrackAround2h";
        getBoundaryRequest getBoundaryRequest=new getBoundaryRequest(url, MapActivity.this);
        getBoundaryRequest.finishUrl(MainActivity.userId);
        pool.execute(getBoundaryRequest);
    }

    /**
     * ????????????
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
        Toast.makeText(this, "????????????????????????~", Toast.LENGTH_SHORT).show();

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
        builder.setTitle("??????????????????")
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
                                //timeThead.drawSwap();????????????????????????
                                //??????button??????
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
                                //TODO: ???????????????????????????
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

        // ?????????????????????????????????????????????
        mDatePicker = new CustomDatePicker(this, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                mTvSelectedDate.setText(DateFormatUtils.long2Str(timestamp, false));
                //????????????
                //ToDo ??????????????????
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
        // ?????????????????????????????????????????????
        mDatePicker.setCancelable(true);
        // ??????????????????
        mDatePicker.setCanShowPreciseTime(false);
        // ?????????????????????
        mDatePicker.setScrollLoop(true);
        // ?????????????????????
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


