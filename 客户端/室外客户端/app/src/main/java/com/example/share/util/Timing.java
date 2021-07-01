package com.example.share.util;

import android.util.Log;

public class Timing {
    private final long startTime;
    private long endTime;
    private final String name;
    public Timing(String name){
        this.name = name;
        startTime = System.currentTimeMillis();
    }
    public void stopAndPrint(FileUtil fileUtil){
        endTime = System.currentTimeMillis();
        long last = endTime - startTime;
        Log.i(name, (last) + "ms | " + (last * FileUtil.getCurCPU() / 1000000f) + "MOps");
        //debugUtil.log(name + ": " + (last) + "ms | " + (last * DebugUtil.getCurCPU() / 1000000f) + "MOps");
    }
    public void stop(){
        endTime = System.currentTimeMillis();
    }
    public long getLastTime(){
        return endTime - startTime;
    }
}
