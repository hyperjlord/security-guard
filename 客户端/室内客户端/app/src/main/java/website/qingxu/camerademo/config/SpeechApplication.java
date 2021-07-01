package website.qingxu.camerademo.config;
/**
 * 配置类
 * 初始化即创建语音配置对象：初始化即创建语音配置对象：建议将初始化放在程序入口处
 */


import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

public class SpeechApplication extends Application {

    @Override
    public void onCreate() {

        //   5ef048e1  为在开放平台注册的APPID  注意没有空格，直接替换即可
        SpeechUtility.createUtility(SpeechApplication.this, "appid=6059d613");

        super.onCreate();
    }
}

