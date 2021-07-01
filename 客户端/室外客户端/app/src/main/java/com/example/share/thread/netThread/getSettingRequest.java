package com.example.share.thread.netThread;

import android.app.Activity;
import android.content.SharedPreferences;
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

public class getSettingRequest implements Runnable{
    private String getUrl;
    private String authorization=null;
    private String cookie=null;
    private String token=null;
    public Activity activity=null;
    public static JSONObject settingObject=null;
    public  getSettingRequest(String url,Activity activities,String token)
    {
        getUrl=url;
        activity=activities;
        this.token=token;
    }

    @Override
    public void run() {
        String result = "";
        BufferedReader in = null;
        try {
            StringBuffer query = new StringBuffer();
            Log.e("senduserInfo","已经发出"+"\n");
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
            if (token != null){
                connection.setRequestProperty("token", token);
            }
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {

                result += line;

            }
//            userInfo =new JSONObject(result);
//            SharedPreferences settings = activity.getSharedPreferences("UserInfo",0);
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putString("ID", userInfo.get("id").toString());
//            Log.e("getuserInfo",userInfo.get("id").toString()+"\n");
//            editor.putString("nickname", userInfo.get("nickname").toString());
//            editor.putString("Username", userInfo.get("nickname").toString());
//            editor.commit();
            //准备存

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
