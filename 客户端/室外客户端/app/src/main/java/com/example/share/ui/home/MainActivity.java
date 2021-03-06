package com.example.share.ui.home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolylineOptions;
import com.example.share.entity.boundarySet;
import com.example.share.service.lockService.LockScreenService;
import com.example.share.service.mapService.BoundaryUpdate.Boundary;
import com.example.share.service.mapService.BoundaryUpdate.Initiate;
import com.example.share.service.mapService.BoundaryUpdate.Operation;
import com.example.share.service.mapService.BoundaryUpdate.Point;
import com.example.share.service.mapService.BoundaryUpdate.Route;
import com.example.share.service.mapService.BoundaryUpdate.RouteSet;
import com.example.share.thread.netThread.PostRequest;
import com.example.share.thread.netThread.getAllTrackRequest;
import com.example.share.thread.netThread.getHistoryRequest;
import com.example.share.thread.netThread.getUserInfoRequest;
import com.example.share.thread.netThread.getWarningRequest;
import com.example.share.thread.netThread.postBoundaryRequst;
import com.example.share.ui.audio.AudioActivity;
import com.example.share.ui.login.LoginActivity;
import com.example.share.R;
import com.example.share.ui.map.MapActivity;
import com.example.share.ui.notice.NoticeActivity;
import com.example.share.util.PointUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {
    MapView mMapView = null;
    AMap aMap = null;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    //sendPoints??????????????????????????????points?????? ?????????realPoints
    public static List<LatLng> sendPoints=new ArrayList<>();
    public static List<LatLng> points = new ArrayList<LatLng>();
    public static List<LatLng> realPoints= new ArrayList<>();
    LatLng lastLatLng=new LatLng(0,0);
    ExecutorService pool = Executors.newFixedThreadPool(5);
    public PowerManager.WakeLock wakeLock;
    public static long userId=0;
//    //????????????
//    public static String timeStr;

    @SuppressLint("InvalidWakeLockTag")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //??????????????????
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.ON_AFTER_RELEASE
                | PowerManager.PARTIAL_WAKE_LOCK, "Tag");
        //??????WakeLock
        wakeLock.acquire(10*60*1000L /*10 minutes*/);

        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        //????????????
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.setLocationSource(this);// ??????????????????
        aMap.setMyLocationEnabled(true);// ?????????true??????????????????????????????????????????false??????????????????????????????????????????????????????false
        getUserInfo();

        long timeStamp=System.currentTimeMillis()/1000;
        try {
            getTrackStart(timeStamp);//?????????????????????
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        //????????????????????????
        Intent startIntent = new Intent(this, LockScreenService.class);
        Log.e("main","???????????????");
        while (getUserInfoRequest.userInfo==null)
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        startService(startIntent);
//        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
//      settings.get
        if(shouldAskPermissions()) {
            askPermissions();
        }
        setUsername();

//        //??????????????????
//        JSONObject sendObject=new JSONObject();
//        try {
//            sendObject.put("account_id",2);
//            sendObject.put("boundary_id",0);
//            boundarySet boundarySet=new boundarySet();
//            sendObject.put("boundary_set_json",boundarySet.finishBoundary_set_json().toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        String url="http://light.qingxu.website:25080/protect/boundaryStorage";
//        postBoundaryRequst postRequest=new postBoundaryRequst(url,sendObject);
//        //???????????????
//        pool.execute(postRequest);
    }

    /**
     * ??????
     * ??????bound??????????????????????????????activity
     * getExtras???put
     * startActivityForResult() ????????????????????????????????????
     * setResult()??????????????????finish???????????? ???????????????
     * ????????????onActivityResult???????????????Res
     *
     * activity??????????????????
     * ??????????????????????????????????????????????????????????????? ???????????????????????????????????????
     * ?????????????????? ??????????????????????????????????????????????????????
     * ??????????????????
     * ??????????????????
     *
     * fragment??????????????????????????????????????????Activity
     * ????????????getActivity ??????Activity
     * ???Fragment ??????findFragmentById()
     * ????????????????????????view ??????return
     * ??????add ??????replace ????????????contain
     * ??????????????????newInstance???????????? ?????????boundle?????????
     * ??????????????????????????????????????????
     *
     *
     * ??????????????????
     * onDetach
     * onDestroy
     *
     * fragment???????????????????????????activity
     * ???????????? ??????+listener???????????????
     *
     * ???????????? ?????????????????????????????????????????????
     *
     * service ??????????????????????????????????????????
     * startService ???activity????????????
     * boundService??? ???activity??????????????????
     * Service??????????????????activity??????
     *
     * ??????????????????????????????y
     */




    public void goToMapPage(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void quitLog(View view) {
        SharedPreferences settings = getSharedPreferences("UserInfo",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ID", "");
        editor.putString("Password", "");
        editor.putString("Username", "");
        editor.putString("nickname", "");
        editor.commit();
        setID();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void setUsername() {
        SharedPreferences settings = getSharedPreferences("UserInfo",0);
        String username = settings.getString("Username", "");
        TextView textView = findViewById(R.id.name);
        textView.setText(username);
    }

    public void notice(View view) {
        Intent intent = new Intent(this, NoticeActivity.class);
        startActivity(intent);
    }

    public void audio(View view) {
        Intent intent = new Intent(this, AudioActivity.class);
        startActivity(intent);
    }

    public void alert(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_alert)
                .setItems(R.array.number_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:{
                                //TODO: call 110
                                break;
                            }
                            case 1:{
                                //TODO: call 119
                                break;
                            }
                            case 2:{
                                //TODO: call 120
                                break;
                            }
                            case 3:{
                                //??????????????????????????????
                                String phone = "18019077681";
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + phone));
                                startActivity(intent);
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

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.CALL_PHONE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    //??????????????????
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.e("AmapMain", "??????:??????");
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// ?????????????????????
                LatLng newLatLng = new LatLng((double) Math.round(amapLocation.getLatitude()*1000000)/1000000,(double) Math.round( amapLocation.getLongitude()*1000000)/1000000);
//                Log.e("????????????",newLatLng.latitude+"   "+newLatLng.longitude);
                long timeStamp=System.currentTimeMillis()/1000;
                if (lastLatLng.latitude!=0) {
                    LatLng oldLatLng = lastLatLng;
                    //?????????????????????????????????
                    if(oldLatLng.latitude==newLatLng.latitude&&oldLatLng.longitude==newLatLng.longitude)
                    {
                        return;
                    }
                    patchLine(oldLatLng, newLatLng);
                    points.add(newLatLng);
                    realPoints.add(newLatLng);
                } else {
                    sendPoints.add(newLatLng);
                    points.add(newLatLng);
                    realPoints.add(newLatLng);
                }
                //????????????
                try {
                    justiceAndSend(timeStamp);
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
    @Override
    protected void onDestroy() {

        pool.shutdown();
        wakeLock.release();
        mlocationClient.stopLocation();
        mlocationClient.onDestroy();
        mlocationClient=null;
        mLocationOption=null;
        setID();
        SharedPreferences settings = getSharedPreferences("UserInfo",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ID", "");
        editor.putString("Password", "");
        editor.putString("Username", "");
        editor.putString("token","");
        editor.commit();
        try {
            while (!pool.awaitTermination(1, TimeUnit.SECONDS)){
                System.out.println("?????????????????????????????????");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //????????????????????????
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
        /**
         * ???????????????
         */
    private void justiceAndSend(long time) throws JSONException {
        lastLatLng=realPoints.get(realPoints.size()-1);
        //ToDo boundarySetList???????????? ??????????????????????????????
        if(boundarySet.boundarySetList.size()>0)
        {

            for(LatLng latLng:sendPoints)
            {

                for(boundarySet boundary:boundarySet.boundarySetList)
                {
                    int boundaryLength=boundary.boundary_set_json.length();
                    for(int i=0;i<boundaryLength;i++)
                    {
                        JSONObject boundaryJson=boundary.boundary_set_json.getJSONObject(i);
                        if(latLng.longitude==(double)boundaryJson.get("longitude")&&latLng.latitude==(double)boundaryJson.get("latitude"))
                        {
                            PointUtils.trackAddPoint(latLng,true,time);
                            //ToDo alert
                             String url="http://stu-project-test.qingxu.website:10008/AUDIO-SERVICE/warning/boundary";
                             getWarningRequest postRequest=new getWarningRequest(url,this,getToken());
                  //???????????????
                        pool.execute(postRequest);
                        }
                    }
                }
            }
            PointUtils.trackAddPoints(realPoints,false,time);

        }
        else
        {

            PointUtils.trackAddPoints(realPoints,false,time);
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
        realPoints.clear();
        if(points.size()>5000)
        {
           for(int i=0;i<100;i++)
           {
               points.remove(0);
           }
        }
        //ToDo ???account_id???????????????0


        if(getID()==0)
            return;
        JSONObject sendObject=PointUtils.trackSetToJson(getID());
       // String url="http://light.qingxu.website:25080/protect/storage";
        String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/storage";
        PostRequest postRequest=new PostRequest(url,sendObject);
        //???????????????
        pool.execute(postRequest);
    }

    /**
     * ????????????????????????????????????????????????track
     * @param time
     */
    private void getTrackStart(long time) throws InterruptedException {
       // String url="http://light.qingxu.website:25080/protect/getAllTrackRecently";
        if(getUserInfoRequest.userInfo==null)
        {
            Thread.sleep(1000);
        }
        String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getAllTrackRecently";
        getAllTrackRequest getAllTrackRequest=new getAllTrackRequest(url, MainActivity.this);
        getAllTrackRequest.finishUrl(getID(),time);
        pool.execute(getAllTrackRequest);
    }

    /**
     * ????????????????????????????????????????????????track
     */
    private void getUserInfo() {

        String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getUserInfoByToken";

        //String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getAllTrackRecently";
        getUserInfoRequest getAllTrackRequest=new getUserInfoRequest(url, MainActivity.this,getToken());
        pool.execute(getAllTrackRequest);
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
                    if(x==from.longitude)
                        continue;
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
                    if(x==from.latitude)
                        continue;
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
                if(x==from.latitude)
                    continue;
                LatLng point = new LatLng( y,x);
                sendPoints.add(point);
            }
        }
    }

    public static void addPoints() throws JSONException {
        Log.e("??????????????????", String.valueOf(points.size())+points.get(0).latitude);
//        for(int i=0;i<points.size();i++)
//        {
//            Log.e("??????????????????", points.get(i).latitude+"     "+points.get(i).longitude);
//        }
        List<LatLng> newList=PointUtils.getListByRes();
        if(newList.size()>0)
        {
            List<LatLng> allPoints=new ArrayList<>();
            allPoints.addAll(newList);
            allPoints.addAll(points);
            points.clear();
            points.addAll(allPoints);
        }
        Log.e("????????????", String.valueOf(points.size())+points.get(0).latitude);
//        for(int i=0;i<points.size();i++)
//        {
//            Log.e("????????????", points.get(i).latitude+"     "+points.get(i).longitude);
//        }
    }

    /**
     *
     * @return
     */
    public  Long getID()
    {
        if(getUserInfoRequest.userInfo!=null&&userId==0)
        {
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            userId=Long.parseLong(settings.getString("ID",""));
        }
        return userId;
    }

    public  String getToken()
    {
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            String token=settings.getString("token","");
            Log.e("getToken",token);
        return token;
    }

    public  void setID()
    {
        userId=0;
    }

    class getUserInfo implements Runnable{

        @Override
        public void run() {

            while (true) {
                long stamp = System.currentTimeMillis();
                String url = "http://stu-project-test.qingxu.website:10008/GUARDIAN-SERVICE/guardian/outdoor/setting";

                //String url="http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getAllTrackRecently";
                getUserInfoRequest getAllTrackRequest = new getUserInfoRequest(url, MainActivity.this, getToken());
                pool.execute(getAllTrackRequest);
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}