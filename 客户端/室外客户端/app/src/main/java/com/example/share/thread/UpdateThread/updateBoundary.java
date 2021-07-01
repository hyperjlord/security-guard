package com.example.share.thread.UpdateThread;


import android.app.Activity;

public class updateBoundary implements Runnable{

    public Activity activity=null;
    public updateBoundary(Activity activities)
    {
        activity=activities;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3600000);
            //ToDo 启动算法逻辑
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
