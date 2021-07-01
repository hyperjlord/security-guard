package com.example.share.service.lockService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.example.share.R;
import com.example.share.ui.home.DetailActivity;

import androidx.core.app.NotificationCompat;

public class LockScreenService extends Service {
    private LockScreenReceiver receiver;
    private IntentFilter filter=new IntentFilter();
    private boolean isNotiShow = false;

    public LockScreenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //动态注册
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_TIME_TICK);

        filter.setPriority(Integer.MAX_VALUE);
        if (null == receiver) {
            receiver = new LockScreenReceiver();
            filter.setPriority(Integer.MAX_VALUE);
            registerReceiver(receiver, filter);

           // buildNotification();
            //Toast.makeText(getApplicationContext(), "开启成功", Toast.LENGTH_LONG).show();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 通知栏选做
     */
//    private void buildNotification() {
//        if (!isNotiShow){ //避免多次显示
//            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            Intent intent = new Intent(this, DetailActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            Notification notification = new NotificationCompat.Builder(this, "default")
//                    .setTicker("APP正在运行")
//                    .setAutoCancel(false)
//                    .setContentTitle("APP正在运行")
//                    .setContentText("运行中")
//                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentIntent(pendingIntent)
//                    .build();
//            manager.notify(1, notification);
//
//            startForeground(0x11, notification);
//
//            isNotiShow = true;
//        }
//    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }
}