package com.yj.util;

public class OSInfo {
    private static String OS = System.getProperty("os.name").toLowerCase();

    private static OSInfo _instance = new OSInfo();

    private OSInfo(){}

    public static boolean isLinux(){
        return OS.indexOf("linux")>=0;
    }

    public static boolean isMacOS(){
        return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")<0;
    }

    public static boolean isMacOSX(){
        return OS.indexOf("mac")>=0&&OS.indexOf("os")>0&&OS.indexOf("x")>0;
    }

    public static boolean isWindows(){
        return OS.indexOf("windows")>=0;
    }

    public static String getStorageDir(){
        if (isLinux()){
            return "/usr/local/audio-data/";
        }
        else if(isWindows()){
            return "D:/test/";
        }
        return "/";
    }
}
