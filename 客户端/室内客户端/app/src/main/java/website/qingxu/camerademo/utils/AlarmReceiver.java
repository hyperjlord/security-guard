package website.qingxu.camerademo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {
    private MediaPlayer playerNetwork;

    @Override
    public void onReceive(Context context, Intent intent) {
        playerNetwork =new MediaPlayer();
        try {
            playerNetwork.setDataSource("http://software-ngineer.oss-cn-shanghai.aliyuncs.com/clouFile/wind_is_rising.mp3?Expires=1624028582&OSSAccessKeyId=TMP.3KfrMjv6wzcenyFb7Kijend8zuVJWnQ7x1PK74rs43fVu9g74Pz7BfwqZpbsDZdQnMT7qgw4sXQjF8puCyWSj5BdX4QJZL&Signature=hk4F7Wef%2FiPaoB8zi5FVG2w0OSw%3D");
            playerNetwork.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerNetwork.start();
//        Intent i = new Intent(context, AlarmActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
    }
}
