package website.qingxu.camerademo.activity.subPage;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

public class AlarmActivity extends Activity {
    private MediaPlayer playerNetwork;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerNetwork =new MediaPlayer();
        Toast.makeText(this, "闹钟闹钟闹钟！！！！", Toast.LENGTH_LONG).show();
        /**
         * 加载音频资源并播放
         */
    }
}
