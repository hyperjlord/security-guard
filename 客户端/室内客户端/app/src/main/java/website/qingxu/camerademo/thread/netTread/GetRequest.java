package website.qingxu.camerademo.thread.netTread;

import android.app.Activity;
import android.util.Log;


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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetRequest implements Runnable{

        private String getUrl;
        private String token;
        private String authorization=null;
        private String cookie=null;
        public String result=null;
        public Activity activity=null;
        public static boolean getRes=false;

        public GetRequest(String url, String token)
        {
            this.getUrl=url;
            this.token=token;
        }
        public void finishUrl(int cameraId)
        {
            if(!getUrl.equals(""))
            {
                getUrl+="?cameraId="+cameraId;
            }
        }
    /**
     * 去掉指定字符串的开头的指定字符
     * @param stream 原始字符串
     * @param trim 要删除的字符串
     * @return
     */
    public String StringStartTrim(String stream, String trim) {
        // null或者空字符串的时候不处理
        if (stream == null || stream.length() == 0 || trim == null || trim.length() == 0) {
            return stream;
        }
        // 要删除的字符串结束位置
        int end;
        // 正规表达式
        String regPattern = "[" + trim + "]*+";
        Pattern pattern = Pattern.compile(regPattern, Pattern.CASE_INSENSITIVE);
        // 去掉原始字符串开头位置的指定字符
        Matcher matcher = pattern.matcher(stream);
        if (matcher.lookingAt()) {
            end = matcher.end();
            stream = stream.substring(end);
        }
        // 返回处理后的字符串
        return stream;
    }


        @Override
        public void run() {
            BufferedReader in = null;
            try {
                StringBuffer query = new StringBuffer();
                Log.e("sendCamera","已经发出"+"\n");

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
                    connection.setRequestProperty("token",token);
                }

                // 建立实际的连接
                connection.connect();
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    //Log.e("getHistoryTrack",line+"\n");
                    result += line;
                }
                result= StringStartTrim (result,"null");
                Log.e("CameraResult", result);

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

