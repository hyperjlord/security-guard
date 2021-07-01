package website.qingxu.camerademo.activity.subPage;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.ws.StreamAVOption;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import website.qingxu.camerademo.LiveUI;
import website.qingxu.camerademo.R;
import website.qingxu.camerademo.service.CameraService.StaticUrl;
import website.qingxu.camerademo.utils.StatusBarUtils;

public class LiveActivity extends AppCompatActivity {
    private static final String TAG = LiveActivity.class.getSimpleName();
    private StreamLiveCameraView mLiveCameraView;
    private StreamAVOption streamAVOption;
    //StatusBarUtils.getRandomAlphaString(3) + '/' +  StatusBarUtils.getRandomAlphaDigitString(5)
    //private String rtmpUrl = "rtmp://stu-project.qingxu.website:20508/live/2";
    private String rtmpUrl = StaticUrl.url;

    private LiveUI mLiveUI;

    private LocationReceiver locationReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        StatusBarUtils.setTranslucentStatus(this);

        /**注册广播接收器*/
        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("location.reportsucc");
        registerReceiver(locationReceiver, filter);


        permissionCheck();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 0:
            case 1:
            case 2:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCheck();
                }
                return;
        }
    }

    private void permissionCheck(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"请授予摄像头权限", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 0);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"请授予录音权限", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"请授予网络权限", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 2);
            return;
        }
        initLiveConfig();
        return;
    }

    /**
     * 设置推流参数
     */
    public void initLiveConfig() {
        mLiveCameraView = (StreamLiveCameraView) findViewById(R.id.stream_previewView);

        //参数配置 start
        streamAVOption = new StreamAVOption();
        streamAVOption.streamUrl = rtmpUrl;
        //参数配置 end

        mLiveCameraView.init(this, streamAVOption);
        mLiveCameraView.addStreamStateListener(resConnectionListener);
        //LinkedList<BaseHardVideoFilter> files = new LinkedList<>();
        //files.add(new GPUImageCompatibleFilter(new GPUImageBeautyFilter()));
        //files.add(new WatermarkFilter(BitmapFactory.decodeResource(getResources(),R.mipmap.live),new Rect(100,100,200,200)));
        //mLiveCameraView.setHardVideoFilter(new HardVideoGroupFilter(files));
        mLiveUI = new LiveUI(this,mLiveCameraView);
        mLiveUI.setUrl(rtmpUrl);
    }

    RESConnectionListener resConnectionListener = new RESConnectionListener() {
        @Override
        public void onOpenConnectionResult(int result) {
            //result 0成功  1 失败
            Toast.makeText(LiveActivity.this,"打开推流连接 状态："+result+ " 推流地址："+ mLiveUI.getUrl(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWriteError(int errno) {
            Toast.makeText(LiveActivity.this,"推流出错,请尝试重连",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCloseConnectionResult(int result) {
            //result 0成功  1 失败
            Toast.makeText(LiveActivity.this,"关闭推流连接 状态："+result,Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationReceiver);
        mLiveCameraView.destroy();
    }

    //内部类，实现BroadcastReceiver
    public class LocationReceiver extends BroadcastReceiver {
        //必须要重载的方法，用来监听是否有广播发送
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction.equals("location.reportsucc")) {
                mLiveCameraView.startStreaming(rtmpUrl);
            }
        }
    }
}
