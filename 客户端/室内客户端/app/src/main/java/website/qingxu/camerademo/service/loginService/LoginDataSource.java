package website.qingxu.camerademo.service.loginService;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import website.qingxu.camerademo.entity.LoginUser;
import website.qingxu.camerademo.thread.netTread.PostLoginRequest;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private Handler mHandler;
    private PostLoginRequest postLoginThread;


    public LoginUser login(String username, String password) {

        try {
            // TODO: handle LoginUser authentication
            // 处理身份验证
            LoginUser user = authentic(username, password);
            return user;
        } catch (Exception e) {
            // 错误信息处理
            Log.e("loginError", e.toString());
            return new LoginUser("","");
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    /**
     * 验证方法
     * @param username
     * @param password
     * @return
     */
    private LoginUser authentic(String username, String password) throws JSONException, InterruptedException {
        // TODO: get user information by username and password
        String url="http://stu-project-test.qingxu.website:10008/ACCOUNT-PROVIDER/v0.0.1/account/login";
        JSONObject loginForm=new JSONObject();
        loginForm.put("password",password);
        loginForm.put("phone",username);

        postLoginThread=new PostLoginRequest(url,loginForm);
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        mHandler.post(postLoginThread);//将线程post到Handler中


        while (true)
        {
            thread.sleep(200);
            if(PostLoginRequest.loginResult!=null)
            {
                break;
            }
        }
        LoginUser user = new LoginUser(username, password);
        return user;
    }

}