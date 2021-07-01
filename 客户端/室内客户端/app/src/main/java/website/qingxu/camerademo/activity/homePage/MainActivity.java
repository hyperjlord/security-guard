package website.qingxu.camerademo.activity.homePage;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import website.qingxu.camerademo.activity.LoginActivity;
import website.qingxu.camerademo.service.AudioService;
import website.qingxu.camerademo.service.CameraService.CameraService;
import website.qingxu.camerademo.utils.AlarmReceiver;
import website.qingxu.camerademo.utils.JsonParser;
import website.qingxu.camerademo.activity.subPage.LiveActivity;
import website.qingxu.camerademo.activity.subPage.MessageActivity;
import website.qingxu.camerademo.activity.subPage.PhoneActivity;
import website.qingxu.camerademo.R;
import website.qingxu.camerademo.utils.SpeechRecognitionUtil;
import website.qingxu.camerademo.utils.WakeupUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    public int tag=0;//用于判断语音命令是否执行

    private Button btnWake;//语音唤醒
    private Button btnStart;//语音识别
    private Button btnMessage;//短信发送
    private Button btnPhone;//打电话
    private Button btnCamera;//打视频
    private Button btnMusic;//播放音频
    private Button btnExit;//退出登录


    private TextView tvResult;//语音识别结果
    private AssetManager assetManager;//asset资源管理
    //音频管理
    private MediaPlayer player1;
    private MediaPlayer player2;
    private MediaPlayer playerNetwork;

    private WakeupUtil wakeUpUtil;//语音唤醒对象
    private SpeechRecognitionUtil recognitionUtil;//语音听写对象

    private AlarmManager alarmManager;  //闹钟管理器

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private SharedPreferences mSharedPreferences;//缓存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保持手机常量
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置布局
        setContentView(R.layout.activity_main);
        //获取button对象
        tvResult = findViewById(R.id.tv_result);
        btnStart = findViewById(R.id.btn_start);
        btnMessage = findViewById(R.id.button_2);
        btnWake = findViewById(R.id.button_3);
        btnPhone = findViewById(R.id.button_4);
        btnCamera=findViewById(R.id.button_5);
        btnMusic=findViewById(R.id.button_6);
        btnExit=findViewById(R.id.button_exit);

        //本地资源的音频初始化。
        player1 = new MediaPlayer();
        player2 = new MediaPlayer();
        playerNetwork =new MediaPlayer();
        assetManager = getAssets();
        AssetFileDescriptor fileDescriptor1 = null;
        AssetFileDescriptor fileDescriptor2 = null;
        try {
            fileDescriptor1 = assetManager.openFd("playOrder.mp3");
            player1.setDataSource(fileDescriptor1.getFileDescriptor(), fileDescriptor1.getStartOffset(), fileDescriptor1.getLength());
            player1.prepare();
            fileDescriptor2 = assetManager.openFd("errorCommand.mp3");
            player2.setDataSource(fileDescriptor2.getFileDescriptor(), fileDescriptor2.getStartOffset(), fileDescriptor2.getLength());
            player2.prepare();
//            playerNetwork.setDataSource("http://software-ngineer.oss-cn-shanghai.aliyuncs.com/clouFile/wind_is_rising.mp3?Expires=1623971240&OSSAccessKeyId=TMP.3KhozXSxnaPyjNkdjyVUpXqfWNvBtdgH82YTY9VFtyD8b63WtxXXCrXHpMagrk7G6yxgJs2L451mY2VHioMZNXhhrHEoHY&Signature=l%2FJSVGzuxXKA41ZFmmCYUsXW0nA%3D");
//
//            playerNetwork.prepare();
//            异步准备
//            playerNetwork.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //权限请求
        initPermission();
        //启动服务
        startService(new Intent(getBaseContext(), AudioService.class));
        startService(new Intent(getBaseContext(), CameraService.class));
        //button点击事件绑定
        btnStart.setOnClickListener(this);
        //设置发送短信按钮监听事件
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });
        //设置打电话按钮监听事件
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhoneActivity.class);
                startActivity(intent);
            }
        });
        //设置打开视频监听事件
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LiveActivity.class);
                startActivity(intent);
            }
        });
        //设置退出登录按钮监听事件
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个SharedPreferences对象
                SharedPreferences settings = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                //实例化SharedPreferences.Editor对象
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("Status", false);
                editor.commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                playerNetwork.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        playerNetwork.start();
//                    }
//                });
                playerNetwork.start();
            }
        });
        //初始化语音听写util
        recognitionUtil = new SpeechRecognitionUtil(this,mRecognizerDialogListener);
        //设置唤醒检测按钮监听事件
        wakeUpUtil = new WakeupUtil(this){
            @Override
            public void wakeUp() throws InterruptedException {//回调函数重写
                Toast.makeText(MainActivity.this, this.resultString, Toast.LENGTH_SHORT).show();
                //播放音频
                player1.start();
                //线程睡一秒
                Thread.sleep(2000);
                //清空缓存
                mIatResults.clear();
                //执行语音听写
                recognitionUtil.recognize();
                this.wake();
            }
        };
        btnWake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakeUpUtil.wake();
            }
        });

        //设置闹钟
        //获取闹钟管理器
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //设置时间
        Calendar ca=Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis());
        Log.e("Alarm","a:"+ca.getTimeInMillis());
        ca.set(Calendar.HOUR_OF_DAY,11);
        ca.set(Calendar.MINUTE,11);
        ca.set(Calendar.SECOND,0);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        //创建pendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0, intent,0);
        //设置闹钟
        if(ca.getTimeInMillis()>=System.currentTimeMillis()){
            alarmManager.set(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(), pendingIntent);
        }
        //alarmManager.set(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(), pendingIntent);
        Log.e(TAG, "b:"+ca.getTimeInMillis());

    }
    @Override
    public void onClick(View v) {
        //执行前清空缓存
        mIatResults.clear();
        //执行语音听写
        recognitionUtil.recognize();
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            //对监听到的results进行分析，分段的
            printResult(results);//结果数据解析
        }
        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showMsg(error.getPlainDescription(true));
        }
    };
    /**
     * 数据解析
     *
     * @param results
     */
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());//每一次结果中的文本
        // 读取json结果中的sn字段，第几句的意思
        String sn = null;
        // 读取json结果中的ls字段，是否是最后一句
        String ls = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
            ls = resultJson.optString("ls");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        /*
            分段识别的结果包括识别词
         */
        if(text.contains("发短信")){
            Intent intent = new Intent(MainActivity.this, MessageActivity.class);
            startActivity(intent);
            tag=1;
        }else if (text.contains("喜欢你")){
            String phone="13131976736";
            Uri nameUri = Uri.parse("smsto:" + phone);
            Intent returnIt = new Intent();
            returnIt.setAction(Intent.ACTION_SENDTO);//发短信的action
            returnIt.setData(nameUri);
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(phone, null, "你好呀，我喜欢你", null, null);
            tag=1;
        }else if(text.contains("电话")){
            String phone="18019087170";
            Intent phoneIntent = new Intent();
            phoneIntent.setAction(Intent.ACTION_CALL);//打电话的action
            phoneIntent.setData(Uri.parse("tel:" + phone));
            startActivity(phoneIntent);
            tag=1;
        }else if(text.contains("打视频")){
            Intent intent = new Intent(MainActivity.this, LiveActivity.class);
            startActivity(intent);
            tag=1;
        }else if(text.contains("走开")){
            wakeUpUtil.stopWake();
            tag=1;
        }
        if(tag==0&&ls.equals("true")){
            tvResult.setText(resultBuffer.toString());//听写结果显示
            player2.start();
        }
        if(ls.equals("true")){
            tag=0;
        }
    }
    /**
     * 提示消息
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (null != mIat) {
//            // 退出时释放连接
//            mIat.cancel();
//            mIat.destroy();
//        }
//    }
    /**
     * android 6.0 以上需要动态申请权限，即在打开手机应用时判断是否拥有权限，若没有，则请求获得
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }
    /**
     * 权限申请回调，可以作进一步处理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    //添加菜单界面
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    //添加菜单逻辑
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.message:
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.phone:
                Intent intent1 = new Intent(MainActivity.this, PhoneActivity.class);
                startActivity(intent1);
                break;
            case R.id.video:
                Intent intent2 = new Intent(MainActivity.this, LiveActivity.class);
                startActivity(intent2);
                break;
            default:
        }
            return true;
    }
}
