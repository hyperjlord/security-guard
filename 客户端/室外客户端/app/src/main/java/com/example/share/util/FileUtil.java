package com.example.share.util;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * 文件工具类
 */
public class FileUtil {
    private static final FileUtil fileUtil = new FileUtil();
    private String filePath;
    private String fileName;
    private FileUtil(){
        fileName = System.currentTimeMillis() + ".json";
    }

    /**
     * 构造函数
     * @param activity 当前运行的activity
     * @param fileName 将要保存的文件夹
     */
    public FileUtil(@NotNull Activity activity, String fileName){
        String workDir = activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        filePath = workDir + "/share/";
        this.fileName = fileName;
    }


    /**
     * 设定activity
     * @param activity
     */
    public void setFilePath(@NotNull Activity activity) {
        String workDir = activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        filePath = workDir + "/share/";
        Log.i("workDir", workDir);
        Log.i("fileName", fileName);
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    /*public void log(String content){
        writeTxtToFile(content, filePath, fileName);
    }*/
    public static FileUtil getDefaultFileUtil(){
        return fileUtil;
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.e("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());

            raf.write(strContent.getBytes());
            Log.e("TestFile", "保存成功");
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }
    public void readFromTxt(String filePath, String fileName) {
        //获取输入流
        String jsonStr = "";
        try {
            File file = new File(filePath+fileName);
            FileReader fileReader = new FileReader(file);
            Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            //返回json字符串
            jsonStr = sb.toString();
            Log.e("readFile",jsonStr);
        } catch (Exception e) {
            Log.e("readFile","error");
        }
    }


    // 生成文件
    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }

    //获取当前CPU频率
    public static int getCurCPU(){
        String CurPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(CurPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (fr != null)
                try{
                    fr.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            if (br != null)
                try{
                    br.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
        }
        return result;
    }

}
