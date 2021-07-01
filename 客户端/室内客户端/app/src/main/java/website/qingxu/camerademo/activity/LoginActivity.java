package website.qingxu.camerademo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import website.qingxu.camerademo.R;
import website.qingxu.camerademo.activity.homePage.MainActivity;
import website.qingxu.camerademo.activity.subPage.MessageActivity;
import website.qingxu.camerademo.entity.UserInfo;
import website.qingxu.camerademo.service.loginService.LoginDataSource;
import website.qingxu.camerademo.service.loginService.UserDataSource;
import website.qingxu.camerademo.thread.netTread.PostLoginRequest;

public class LoginActivity extends AppCompatActivity  {
    private EditText username;//用户名
    private EditText password;//密码
    private Button login;//登录
    private Button sign_in;//注册


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //首先判断是否已经登录，如果已经登录，直接跳入用户主页面
        if(hasLogged()){
            try {
                toMain();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        sign_in=findViewById(R.id.sign_in);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = username.getText().toString();//用户名
                String pass_word = password.getText().toString();//密码
                //非空验证
                if (user_name.isEmpty() || pass_word.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //检测用户信息是否正确
                LoginDataSource check=new LoginDataSource();
                check.login(user_name,pass_word);
                try {
                    JSONObject loginRes=new JSONObject(PostLoginRequest.loginResult);
                    if(loginRes.getInt("stateCode")!=0){
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        PostLoginRequest.loginResult=null;
                    }else{
                        saveInfo(true,user_name,pass_word,loginRes.getString("token"));
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        toMain();
                        finish();
                    }
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //进入主界面
    public void toMain() throws JSONException, InterruptedException {
        Intent intent = new Intent(this, MainActivity.class);
        PostLoginRequest.loginResult=null;
        /**
         * 进入主界面之前，请求获取相关用户信息及音频资源
         */
//        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
//        UserDataSource userDataSource=new UserDataSource();
//        UserInfo userInfo=userDataSource.getInfo(settings.getString("token",""));
//        SharedPreferences userSettings = getSharedPreferences("UserInformation",0);
//        SharedPreferences.Editor editor = userSettings.edit();
//        //将获取过来的值放入文件
//        editor.putString("selfPhone",userInfo.getSelfPhone());
//        editor.putString("nickName", userInfo.getNickName());
//        editor.putString("guardName", userInfo.getGuardName());
//        editor.putString("phone",userInfo.getGuardPhone());
//        //提交
//        editor.commit();
        /****************************************************************/
        startActivity(intent);
    }

    //将登录信息存入本地缓存
    private void saveInfo(boolean status, String password, String username,String token) {
        //创建一个SharedPreferences对象
        SharedPreferences settings = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = settings.edit();
        //将获取过来的值放入文件
        editor.putBoolean("Status", status);
        editor.putString("Password", password);
        editor.putString("Username", username);
        editor.putString("token",token);
        //提交
        editor.commit();
    }
    //判断是否已经登录
    private boolean hasLogged() {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        Log.e("token", settings.getString("token",""));
        if(settings != null && settings.getBoolean("Status", false)) return true;
        else return false;
    }


}

