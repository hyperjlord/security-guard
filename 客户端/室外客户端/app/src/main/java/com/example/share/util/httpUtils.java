package com.example.share.util;


import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * http工具类
 */
public class httpUtils implements Runnable {

    //get请求所有的参数全放在URL中
    public static String sendGetRequest(String urlParam) {
        HttpURLConnection con = null;
        BufferedReader buffer = null;
        StringBuffer resultBuffer = null;
        try {
            URL url = new URL(urlParam);
            //得到连接对象
            con = (HttpURLConnection) url.openConnection();
            //设置请求类型
            con.setRequestMethod("GET");
            //设置请求需要返回的数据类型和字符集类型
            con.setRequestProperty("Content-Type", "application/json;charset=GBK");
            //允许写出
            con.setDoOutput(true);
            //允许读入
            con.setDoInput(true);
            //不使用缓存
            con.setUseCaches(false);
            //得到响应码
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //得到响应流
                InputStream inputStream = con.getInputStream();
                //将响应流转换成字符串
                resultBuffer = new StringBuffer();
                String line;
                buffer = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
                while ((line = buffer.readLine()) != null) {
                    resultBuffer.append(line);
                }
                return resultBuffer.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sendPostRequest(String postUrl, JSONObject jsonObject) {
        BufferedReader reader = null;
        try {
            //new一个访问的url
            URL url = new URL(postUrl);
            //创建HttpURLConnection 实例
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //提交数据的方式
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            //设置超时时间
            connection.setConnectTimeout(8000);//连接超时
            //读取超时
            connection.setReadTimeout(8000);
            //连接打开输出流
            OutputStream os = connection.getOutputStream();

            //jsons数据拼接
            JSONObject json = jsonObject;
            //把数据改为中文编码（接收时 直接接收这个jsonObject.）
            String dataEncrypt = URLEncoder.encode(json.toString(), "UTF-8");
            //数据写入输出流（发送）
            os.write(dataEncrypt.getBytes());
            if (connection.getResponseCode() == 200) {
                //接收服务器输入流信息
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                //拿到信息
                String result = br.readLine();
                Log.i("返回数据：", result);
                is.close();
                os.close();
                connection.disconnect();
                return result;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void run() {

    }
}



