package website.qingxu.camerademo;

import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.lake.librestreaming.core.listener.RESScreenShotListener;
import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import me.lake.librestreaming.ws.filter.hardfilter.FishEyeFilterHard;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageBeautyFilter;
import me.lake.librestreaming.ws.filter.hardfilter.extra.GPUImageCompatibleFilter;
import website.qingxu.camerademo.activity.subPage.LiveActivity;
import website.qingxu.camerademo.service.CameraService.StaticUrl;

public class LiveUI implements View.OnClickListener {

    private LiveActivity activity;
    private StreamLiveCameraView liveCameraView;
    private String url = "";
   // private String url = "rtmp://stu-project.qingxu.website:20508/live/1-JPn2N7hNrx";
    //
    private String password = "testRoom";
    int filterNum = 0;
    boolean isMirror = false;

    private Button btnStartStreaming;
    private Button btnStopStreaming;
    private Button btnStartRecord;
    private Button btnStopRecord;
    private Button btnFliter;
    private Button btnSwapCamera;
    private Button btnScreenshot;
    private Button btnMirror;
    private Button btnRoom;
    private Button btnUrl;
    private EditText etUrl;

    private ImageView imageView;

    public LiveUI(LiveActivity liveActivity, StreamLiveCameraView liveCameraView) {
        this.activity = liveActivity;
        this.liveCameraView = liveCameraView;

        init();
    }

    private void init() {
        btnStartStreaming = (Button) activity.findViewById(R.id.btn_startStreaming);
        btnStartStreaming.setOnClickListener(this);
        //btnStartStreaming.performClick();

        btnStopStreaming = (Button) activity.findViewById(R.id.btn_stopStreaming);
        btnStopStreaming.setOnClickListener(this);

        btnStartRecord = (Button) activity.findViewById(R.id.btn_startRecord);
        btnStartRecord.setOnClickListener(this);

        btnStopRecord = (Button) activity.findViewById(R.id.btn_stopRecord);
        btnStopRecord.setOnClickListener(this);

        btnFliter = (Button) activity.findViewById(R.id.btn_filter);
        btnFliter.setOnClickListener(this);

        btnSwapCamera = (Button) activity.findViewById(R.id.btn_swapCamera);
        btnSwapCamera.setOnClickListener(this);

        btnScreenshot = (Button) activity.findViewById(R.id.btn_screenshot);
        btnScreenshot.setOnClickListener(this);

        btnMirror = (Button) activity.findViewById(R.id.btn_mirror);
        btnMirror.setOnClickListener(this);

//        btnRoom = (Button) activity.findViewById(R.id.btn_room);
//        btnRoom.setOnClickListener(this);

        btnUrl = (Button) activity.findViewById(R.id.btn_url);
        btnUrl.setOnClickListener(this);

//        etUrl = (EditText) activity.findViewById(R.id.et_url);
//        etUrl.setOnEditorActionListener(
//            new TextView.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    boolean handled = false;
//                    if (actionId == EditorInfo.IME_ACTION_DONE) {
////                        setUrl(etUrl.getText().toString());
//                        handled = true;
//                    }
//                    return handled;
//                }
//            });


        imageView = (ImageView) activity.findViewById(R.id.iv_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_startStreaming://????????????
                if (!liveCameraView.isStreaming()) {
                    liveCameraView.startStreaming(url);
                }
                break;
            case R.id.btn_stopStreaming://????????????
                if (liveCameraView.isStreaming()) {
                    liveCameraView.stopStreaming();
                }
                break;
            case R.id.btn_startRecord://????????????
                if (!liveCameraView.isRecord()) {
                    Toast.makeText(activity, "??????????????????", Toast.LENGTH_SHORT).show();
                    liveCameraView.startRecord();
                }
                break;
            case R.id.btn_stopRecord://????????????
                if (liveCameraView.isRecord()) {
                    liveCameraView.stopRecord();
                    Toast.makeText(activity, "????????????????????????", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_filter://????????????
                BaseHardVideoFilter baseHardVideoFilter = null;
                switch (filterNum) {
                    case 0:
                        baseHardVideoFilter = new GPUImageCompatibleFilter(new GPUImageBeautyFilter());
                        filterNum++;
                        Toast.makeText(activity, "????????????", Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        baseHardVideoFilter = new FishEyeFilterHard();
                        Toast.makeText(activity, "????????????", Toast.LENGTH_SHORT).show();
                        filterNum++;
                        break;

                    case 2:
                        filterNum = 0;
                        Toast.makeText(activity, "?????????", Toast.LENGTH_SHORT).show();
                        break;
                }
                liveCameraView.setHardVideoFilter(baseHardVideoFilter);
                break;
            case R.id.btn_swapCamera://???????????????
                liveCameraView.swapCamera();
                break;
            case R.id.btn_screenshot://??????
                liveCameraView.takeScreenShot(new RESScreenShotListener() {
                    @Override
                    public void onScreenShotResult(Bitmap bitmap) {
                        if (bitmap != null) {
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageBitmap(bitmap);
                        }

                    }
                });
                break;
            case R.id.btn_mirror://??????
                if (isMirror) {
                    liveCameraView.setMirror(true, false, false);
                } else {
                    liveCameraView.setMirror(true, true, true);
                }
                isMirror = !isMirror;
                break;
            case R.id.btn_url:
                setUrl(etUrl.getText().toString());
                break;

//            case R.id.btn_room://???????????????
//                if (roomNum >= 5) {
//                    roomNum = 0;
//                } else {
//                    roomNum++;
//                }
//                Toast.makeText(activity, "?????????????????????" + Integer.toString(roomNum), Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }

//    public String getRoomNum() {
//        return Integer.toString(roomNum);
//    }
//
//    public String getUrl() {
//        return rtmpUrl + getRoomNum() + '/' + password;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public void setRtmpUrl(String rtmpUrl) {
//        this.rtmpUrl = rtmpUrl;
//    }

    public void setUrl(String url) {
        this.url = url;
        Toast.makeText(activity, "????????????????????????" + url, Toast.LENGTH_SHORT).show();
    }

    public String getUrl() {
        return url;
    }
}
