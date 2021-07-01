package com.example.share.thread.netThread;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class getTrackRequest implements Runnable{
    private String getUrl;
    private String authorization=null;
    private String cookie=null;
    public getTrackRequest(String url)
    {
        getUrl=url;
    }
    public void finishUrl(int account_id,long time)
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
                result += line;
            }
            Log.e("getTrack",result);
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
