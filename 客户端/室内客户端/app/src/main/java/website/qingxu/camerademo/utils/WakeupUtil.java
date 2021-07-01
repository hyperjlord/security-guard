package website.qingxu.camerademo.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;

import org.json.JSONException;
import org.json.JSONObject;

import website.qingxu.camerademo.R;

/**
 * Created by Administrator on 2018/2/28.
 * 讯飞语音唤醒
 */

public abstract class WakeupUtil {
    /**
     * 唤醒的回调
     */
    public abstract void wakeUp() throws InterruptedException;

    // Log标签
    private static final String TAG = "WakeUpUtil";

    // 上下文
    private Context mContext;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    //本地资源
    private String resPath;

    //唤醒门限值（门限值越低，越容易唤醒）
    private int curThresh = 800;

    public WakeupUtil(Context context) {
        mContext = context;
        // 加载识唤醒地资源，resPath为本地识别资源路径
        StringBuffer param = new StringBuffer();
        resPath = ResourceUtil.generateResourcePath(context, RESOURCE_TYPE.assets, "ivw/6059d613.jet");
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(context, null);
    }

    /**
     * 获取唤醒词功能
     *
     * @return 返回文件位置
     */
    private String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(mContext, RESOURCE_TYPE.assets, "ivw/" + mContext.getString(R.string.IflytekAPP_id) + ".jet");
        return resPath;
    }

    /**
     * 唤醒
     */
    public void wake() {
        // 非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            // textView.setText(resultString);
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 设置唤醒资源路径
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH, resPath);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            //设置NOTIFY_RECORD_DATA以实时通过onEvent返回录音音频流字节
            mIvw.setParameter(SpeechConstant.NOTIFY_RECORD_DATA,"1");
            mIvw.startListening(mWakeuperListener);
        } else {
            Toast.makeText(mContext, "唤醒未初始化", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopWake() {
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.stopListening();
        } else {
            Toast.makeText(mContext, "唤醒未初始化", Toast.LENGTH_SHORT).show();
        }
    }

    public String resultString = "";
    //唤醒词监听
    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 " + text);
                buffer.append("\n");
                buffer.append("【操作类型】" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】" + object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString = buffer.toString();
                stopWake();
                wakeUp();
            } catch (JSONException | InterruptedException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError error) {
            Log.i(TAG, error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
            Log.i(TAG, "开始说话");
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            //待处理
//            switch(eventType){
//                case SpeechEvent
//                        .EVENT_RECORD_DATA:
//                    final byte[] audio = obj.getByteArray(SpeechEvent.KEY_EVENT_RECORD_DATA);
//            }
        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };

}
