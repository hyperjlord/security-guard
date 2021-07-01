package com.example.share.thread.netThread;

import android.app.Activity;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.example.share.ui.home.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class getAllTrackRequest implements Runnable{
    private String getUrl;
    private String authorization=null;
    private String cookie=null;
    public Activity activity=null;
    public static JSONArray allTrackArray=null;
    public static boolean getRes=false;
    public getAllTrackRequest(String url,Activity activities)
    {
        getUrl=url;
        activity=activities;
    }
    public void finishUrl(long account_id,long time)
    {
        if(!getUrl.equals(""))
        {
            getUrl+="?account_id="+account_id+"&time="+time;
        }
    }



    @Override
    public void run() {
        String result = "";
        BufferedReader in = null;
        try {
            StringBuffer query = new StringBuffer();
            Log.e("send","已经发出"+"\n");
            URL realUrl = new URL(getUrl);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            if (authorization != null){
                connection.setRequestProperty("Authorization", authorization);
            }
            if (cookie != null){
                connection.setRequestProperty("Cookie", cookie);
            }
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                Log.e("getAllTrack",line+"\n");
                result += line;

            }
            allTrackArray=new JSONArray(result);
            MainActivity.addPoints();
//            JSONArray getArray1=new JSONArray(getArray.get(0).toString());
            //ToDo 记得来用这个
            //文件存储（仅测试）

            //FileUtil.getDefaultFileUtil().writeTxtToFile(result,activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(),"track.json");
            //文件读取（仅测试）

            // FileUtil.getDefaultFileUtil().readFromTxt(activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(),"track.json");

//            Log.e("getAllTrack",result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }
}
