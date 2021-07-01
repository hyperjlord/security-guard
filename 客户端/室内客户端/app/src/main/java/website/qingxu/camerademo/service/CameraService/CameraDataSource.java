package website.qingxu.camerademo.service.CameraService;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import website.qingxu.camerademo.entity.CameraHeart;
import website.qingxu.camerademo.entity.LoginUser;
import website.qingxu.camerademo.entity.UserInfo;
import website.qingxu.camerademo.thread.netTread.GetRequest;
import website.qingxu.camerademo.thread.netTread.PostLoginRequest;
import website.qingxu.camerademo.thread.netTread.PostRequest;

public class CameraDataSource {
    private Handler mHandler;
    private PostRequest postThread;
    private GetRequest getThread;
    private PostRequest postLoginThread;

    /**
     *
     * @param token
     * @return
     * @throws JSONException
     * @throws InterruptedException
     */
    //获取心跳消息
    public CameraHeart getInfo(int cameraId,String token) throws JSONException, InterruptedException {
        // TODO: get user information by username and password
        String url="http://stu-project-test.qingxu.website:10008/INDOOR-PROVIDER/v0.0.1/stream/camera-heartbeat";
        getThread = new GetRequest(url,token);
        Log.e("token", token);
        getThread.finishUrl(cameraId);
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        mHandler.post(getThread);//将线程post到Handler中
        while (true)
        {
            thread.sleep(200);
            if(getThread.result!=null)
            {
                Log.e("!!!!!!!", "bugbugbubgbgugbugbugbug: ");
                break;
            }
        }
        JSONObject Result = new JSONObject(getThread.result);
        if(Result.getString("command").equals("null")){
            CameraHeart cameraHeart = new CameraHeart(
                    null,
                    Result.getInt("stateCode"),
                    Result.getString("msg")
            );
            return cameraHeart;
        }else{
            CameraHeart cameraHeart = new CameraHeart(
                    Result.getJSONObject("command").getJSONObject("params").getString("RTMPurl"),
                    Result.getInt("stateCode"),
                    Result.getString("msg")
            );
            return cameraHeart;
        }
    }
    //发送接收消息
    public void sendSuccess(int cameraId,String token) throws JSONException, InterruptedException {
        // TODO: get user information by username and password
        String url="http://stu-project-test.qingxu.website:10008/INDOOR-PROVIDER/v0.0.1/stream/stream-success";
        JSONObject successForm=new JSONObject();
        successForm.put("cameraId",cameraId);
        postThread=new PostRequest(url,successForm,token);
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        mHandler.post(postThread);//将线程post到Handler中

        while (true)
        {
            thread.sleep(200);
            if(postThread.Result!=null)
            {
                break;
            }
        }

    }
}
