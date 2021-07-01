package com.xu;

import java.io.File;

public class testNative {
    static File currentDir = new File("");
    static
    {
        System.load(currentDir.getAbsolutePath() + "\\JavaDLL.dll");
    }
    public native void result(String str);
    public native void test();
    public native int danger();
    public static void main(String[] args)
    {
      testNative test=new testNative();
      String str=currentDir.getAbsolutePath() + "\\ss.AVI";
      test.result(str);
    }

}
