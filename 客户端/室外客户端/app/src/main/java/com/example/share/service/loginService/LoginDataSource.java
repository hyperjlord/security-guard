package com.example.share.service.loginService;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;


import com.example.share.entity.LoggedInUser;
import com.example.share.thread.netThread.postLoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private Handler mHandler;
    private postLoginRequest postLoginThead;


    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser user = authentic(username, password);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
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
    private LoggedInUser authentic(String username, String password) throws JSONException, InterruptedException {
        // TODO: get user information by username and password
        String url="http://stu-project-test.qingxu.website:10008/ACCOUNT-PROVIDER/v0.0.1/account/login";
        JSONObject loginForm=new JSONObject();
        loginForm.put("password",password);
        loginForm.put("phone",username);

        postLoginThead=new postLoginRequest(url,loginForm);
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        mHandler.post(postLoginThead);//将线程post到Handler中



        while (true)
        {
            thread.sleep(200);
            if(postLoginRequest.loginResult!=null)
            {
                break;
            }
        }
        LoggedInUser user = new LoggedInUser(username, username);
        return user;
    }

}