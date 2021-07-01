package website.qingxu.camerademo.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class SpeechRecognitionUtil {

    // 在活动创建语音识别时传入context
    private Context context;
    // Log标签
    private static final String TAG = "SpeechRecognitionUtil";
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 听写UI的监听器
    private RecognizerDialogListener mRecognizerDialogListener;

    // 引擎类型，有云端和本地
    // 这里采用云端引擎
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 识别语言
    private String language = "zh_cn";
    //结果内容数据格式
    private String resultType = "json";

    public SpeechRecognitionUtil(Context context,RecognizerDialogListener mRecognizerDialogListener){
        this.context=context;
        this.mRecognizerDialogListener=mRecognizerDialogListener;
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        this.mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        this.mIatDialog = new RecognizerDialog(context, mInitListener);
        //参数设置
        //预留的参数设置
    }

    public void recognize(){
        if( null == mIat ){
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            ToastUtil.showMessage( context,"创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
            return;
        }
        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();;
    }
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                ToastUtil.showMessage(context,"初始化失败，错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };
    /**
     * 判断某一活动是否在栈顶
     * @param cls
     * @param context
     * @return boolean
     */
    private boolean isActivityTop(Class cls,Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }
}
