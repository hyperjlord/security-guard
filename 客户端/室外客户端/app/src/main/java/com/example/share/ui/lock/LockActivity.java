package com.example.share.ui.lock;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.example.share.R;
import com.example.share.service.lockService.LockScreenReceiver;
import com.example.share.service.lockService.LockScreenService;
import com.example.share.thread.UpdateThread.timeUpdate;
import com.example.share.ui.audio.AudioActivity;
import com.example.share.ui.notice.NoticeActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.xml.sax.Parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LockActivity extends AppCompatActivity  {


    private KeyGuardReceiver mKeyGuardReceiver;
    private TextView mTimeView;
    private TextView mDateView;
    private SlideLock slideLock;
    private ImageView img;
    private PowerManager.WakeLock wakeLock;
    private timeUpdate timeThead;
    private Handler mHandler;


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.ON_AFTER_RELEASE
                | PowerManager.PARTIAL_WAKE_LOCK, "Tag");
        //申请WakeLock
        wakeLock.acquire(10*60*1000L /*10 minutes*/);
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );//可使其保持常亮 以保证我们的算法运行
        setContentView(R.layout.activity_lock);
        slideLock = (SlideLock) findViewById(R.id.slideLock);

        mTimeView = (TextView) findViewById(R.id.time);
        mDateView = (TextView) findViewById(R.id.date);

        timeThead=new timeUpdate(mDateView,mTimeView);
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        mHandler.post(timeThead);//将线程post到Handler中

        slideLock.setOnUnlockListener(
                new OnUnlockListener(){
            @Override
            public void setUnlock(boolean unlock) {
                if (unlock){
                    slideLock.setVisibility(View.GONE);
                    //img.setVisibility(View.VISIBLE);
                    finish();
                    LockScreenReceiver.runAble=false;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.e("lock","已暂停");

    }
    @Override
    protected void onStop() {
        Log.e("lock","已暂停");
        super.onStop();
    }

    @Override
    public void onBackPressed() {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_BACK: {
                return true;
            }
            case KeyEvent.KEYCODE_MENU: {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void registerKeyGuardReceiver() {
        if (null == mKeyGuardReceiver) {
            mKeyGuardReceiver = new KeyGuardReceiver();
            registerReceiver(mKeyGuardReceiver, new IntentFilter());
        }
    }

    private void unregisterKeyGuardReceiver() {
        if (mKeyGuardReceiver != null) {
            unregisterReceiver(mKeyGuardReceiver);
        }
    }
    
    class KeyGuardReceiver extends BroadcastReceiver {

        static final String SYSTEM_REASON = "reason";
        static final String SYSTEM_HOME_KEY = "homekey";// home key
        static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (reason != null) {
                    if (reason.equals(SYSTEM_HOME_KEY)) {
                        finish();
                    } else if (reason.equals(SYSTEM_RECENT_APPS)) {
                    }
                }
            } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED) || action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                finish();
            }
        }
    }

    private void showToast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(timeThead);
        wakeLock.release();
        unregisterKeyGuardReceiver();
        super.onDestroy();
    }

    public void notice(View view) {
        Intent intent = new Intent(this, NoticeActivity.class);
        startActivity(intent);
    }

    public void audio(View view) {
        Intent intent = new Intent(this, AudioActivity.class);
        startActivity(intent);
    }

    public void alert(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_alert)
                .setItems(R.array.number_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:{
                                //TODO: call 110
                                break;
                            }
                            case 1:{
                                //TODO: call 119
                                break;
                            }
                            case 2:{
                                //TODO: call 120
                                break;
                            }
                            case 3:{
                                String phone = "18019077681";
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + phone));
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_alert_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.show();
    }

}