package com.example.share.service.lockService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.share.ui.lock.LockActivity;
import com.example.share.ui.login.LoginActivity;
import com.example.share.ui.map.MapActivity;

import org.xml.sax.Parser;

public class LockScreenReceiver extends BroadcastReceiver {

    public static boolean runAble=false;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_OFF.equals(action)&&!runAble) {
          Intent lockScreen = new Intent(context, LockActivity.class);
//          Log.e("启动","确实启动");
                lockScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
               context.startActivity(lockScreen);
            runAble=true;
        }
        else
        {

        }
    }
}
