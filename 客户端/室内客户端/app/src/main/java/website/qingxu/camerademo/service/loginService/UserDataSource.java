package website.qingxu.camerademo.service.loginService;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import website.qingxu.camerademo.entity.LoginUser;
import website.qingxu.camerademo.entity.UserInfo;
import website.qingxu.camerademo.thread.netTread.PostLoginRequest;
import website.qingxu.camerademo.thread.netTread.PostRequest;

public class UserDataSource {

    private Handler mHandler;
    private PostRequest postThread;

    /**
     *
     * @param token
     * @return
     * @throws JSONException
     * @throws InterruptedException
     */
    public UserInfo getInfo(String token) throws JSONException, InterruptedException {
        // TODO: get user information by username and password
        String url="http://stu-project-test.qingxu.website:10008/ACCOUNT-PROVIDER/v0.0.1/account/login";
        JSONObject getInfoForm=new JSONObject();
        getInfoForm.put("token",token);

        postThread=new PostRequest(url,getInfoForm);
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
        JSONObject Result=new JSONObject(postThread.Result);
        UserInfo userInfo=new UserInfo(
                Result.getString("selfPhone"),
                Result.getString("nickName"),
                Result.getString("guardPhone"),
                Result.getString("guardName"));
        return userInfo;
    }


}
