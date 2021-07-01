package website.qingxu.camerademo.service.CameraService;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import me.lake.librestreaming.client.RESClient;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import website.qingxu.camerademo.R;
import website.qingxu.camerademo.activity.homePage.MainActivity;
import website.qingxu.camerademo.activity.subPage.LiveActivity;
import website.qingxu.camerademo.entity.CameraHeart;

public class CameraService extends Service {

    //传入上下文
    private Context context;
    //串流工具
    private static RESClient resClient;

    private Handler mHandler;
    private Timer timer = new Timer();
    private CameraDataSource cameraDataSource;
    private SharedPreferences settings;
    private StreamLiveCameraView liveCameraView;
    @Override
    public void onCreate() {
        super.onCreate();

        resClient=new RESClient();
        context=this;
        cameraDataSource = new CameraDataSource();
        settings = getSharedPreferences("UserInfo", 0);

        Toast.makeText(this, "相机心跳检测开始", Toast.LENGTH_LONG).show();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                if(msg.obj==null){
                    Log.e("url", "null");
                }else{
                    Log.e("url", msg.obj.toString());
                }
                Log.e("isActivityTop", ""+isActivityTop(LiveActivity.class,context));
                if (msg.what == 1&&!isActivityTop(LiveActivity.class,context)) {
                    try {
                        StaticUrl.url=msg.obj.toString();
                        Intent intent = new Intent(CameraService.this, LiveActivity.class);
                        startActivity(intent);
                        //发送广播
                        Intent intent1 = new Intent();
                        intent1.putExtra("key", "test");
                        intent1.setAction("location.reportsucc");
                        sendBroadcast(intent1);
                        cameraDataSource.sendSuccess(1,settings.getString("token",""));
                        //liveCameraView = (StreamLiveCameraView) findViewById(R.id.stream_previewView);
                       // liveCameraView.startStreaming(msg.obj.toString());
                        //告诉监护端已开启
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    if(msg.what == 0&&isActivityTop(LiveActivity.class,context)){
                        Intent intent = new Intent(CameraService.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                super.handleMessage(msg);
            }
        };
        TimerTask task = new TimerTask(){
            public void run() {
                Message message = new Message();
                try {
                    CameraHeart cameraHeart=cameraDataSource.getInfo(1,settings.getString("token",""));
                    //心跳检测的url
                    message.obj=cameraHeart.rmpUrl;
                    if(cameraHeart.state==0){
                        message.what=1;
                    }else{
                        message.what=0;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //message.what=0;
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task, new Date(),15000);
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "定时提醒服务已经开始", Toast.LENGTH_LONG).show();
        // Let it continue running until it is stopped.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "定时提醒服务已经停止", Toast.LENGTH_LONG).show();
    }
    /**
     * 判断某一活动是否在栈顶
     * @param cls
     * @param context
     * @return boolean
     */
    private boolean isActivityTop(Class cls, Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }


}
