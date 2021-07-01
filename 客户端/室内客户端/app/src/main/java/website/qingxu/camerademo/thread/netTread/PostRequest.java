package website.qingxu.camerademo.thread.netTread;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostRequest implements Runnable {
    public String postUrl;
    public JSONObject postJsonObject;
    public String token;

    public String Result=null;
    public PostRequest(String posturl, JSONObject jsonObject) {
        postUrl = posturl;
        postJsonObject = jsonObject;
    }
    public PostRequest(String posturl, JSONObject jsonObject,String token) {
        postUrl = posturl;
        postJsonObject = jsonObject;
        this.token=token;
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
        BufferedReader reader = null;
        PrintWriter out = null;
        try {
            //new一个访问的url
            URL url = new URL(postUrl);
            //创建HttpURLConnection 实例
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //提交数据的方式
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setDoInput(true);
//                connection.setRequestProperty("Connection", "Keep-Alive");
//                connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            if (token != null){
                connection.setRequestProperty("token",token);
            }
//                // Post 请求不能使用缓存
//                connection.setUseCaches(false);
//                //设置缓冲大小，当流达到这个值后输出，参数为0则代表采用操作系统默认值
//                connection.setChunkedStreamingMode(0);
            connection.connect();
            //设置超时时间
            connection.setConnectTimeout(5000);//连接超时

            //读取超时
            connection.setReadTimeout(5000);
            out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数
            out.print(postJsonObject);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line, result = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            Result=StringStartTrim(result,"null");
            Log.e("loginResult", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}