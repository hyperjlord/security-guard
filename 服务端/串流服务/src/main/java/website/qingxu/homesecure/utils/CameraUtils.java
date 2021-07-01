package website.qingxu.homesecure.utils;


import java.security.SecureRandom;
import java.util.Arrays;

public class CameraUtils {
    private CameraUtils(){}
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARSET ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
    public static final String BASE_URL = "stu-project.qingxu.website";
    public static int getCameraLimit(int accountType){
        switch (accountType) {
            case 100:
                return 1000;
            case 1000:
                return 10000;
            default:
                return 99;
        }
    }
    public static String genInitialPass(int length){
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }
    public static String transToRTMPUrl(long cameraId, String pass, String room){
        return String.format("rtmp://%s:20508/%s/%d-%s",
                BASE_URL,
                room,
                cameraId,
                pass);
    }
    public static String transToHTTPUrl(long cameraId, String pass, String room){
        return String.format("http://%s:20509/%s/%d-%s.flv",
                BASE_URL,
                room,
                cameraId,
                pass);
    }
    public static String transToStreamStatusKey(long cameraId){
        return "StreamStatus:" + Long.toString(cameraId);
    }
}
