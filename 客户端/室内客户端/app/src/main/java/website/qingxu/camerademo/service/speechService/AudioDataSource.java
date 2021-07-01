package website.qingxu.camerademo.service.speechService;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import website.qingxu.camerademo.entity.LoginUser;
import website.qingxu.camerademo.thread.netTread.PostAudioRequest;
import website.qingxu.camerademo.thread.netTread.PostLoginRequest;

public class AudioDataSource {
    private Handler mHandler;
    private PostAudioRequest postAudioThread;

    public String postAudio(String token){
        try {
            // TODO: handle LoginUser authentication
            // 处理身份验证
            String result = request(token);
            return result;
        } catch (Exception e) {
            // 错误信息处理
            Log.e("AudioError", e.toString());
            return "";
        }
    }
    //发送请求
    private String request(String token) throws JSONException, InterruptedException {
        // TODO: get user information by username and password
        String url="http://stu-project-test.qingxu.website:10008/ACCOUNT-PROVIDER/v0.0.1/account/login";
        JSONObject AudioForm=new JSONObject();
        AudioForm.put("token",token);

        postAudioThread=new PostAudioRequest(url,AudioForm);
        HandlerThread thread = new HandlerThread("MyHandlerThread2");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        mHandler.post(postAudioThread);//将线程post到Handler中

        //是否异步看情况处理
        while (true)
        {
            thread.sleep(200);
            if(postAudioThread.postResult!=null)
            {
                break;
            }
        }
        return postAudioThread.postResult;
    }

}
