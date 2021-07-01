package com.example.share.ui.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.share.R;



import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AudioRecordActivity extends AppCompatActivity implements Runnable {

    private Button mBtnStartRecord, mBtnStopRecord;
    //指定音频源 这个和MediaRecorder是相同的 MediaRecorder.AudioSource.MIC指的是麦克风
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;
    //指定采样率 （MediaRecorder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private static final int mSampleRateInHz = 8000;
    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private static final int mChannelConfig = AudioFormat.CHANNEL_IN_STEREO;
    //指定音频量化位数 ,在AudioFormat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mBufferSizeInBytes;
    int mPlayBuffSize;

    private File mRecordingFile;//储存AudioRecord录下来的文件
    private boolean isRecording = false; //true表示正在录音
    private AudioRecord mAudioRecord = null;
    private AudioTrack audioTrack = null;

    private File mFileRoot = null;//文件目录
    //存放的目录路径名称
    private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudioRecordFile";
    //保存的音频文件名
    private static final String mFileName = "audiotest.pcm";
    //缓冲区中数据写入到数据，因为需要使用IO操作，因此读取数据的过程应该在子线程中执行。
    private Thread mThread;
    private DataOutputStream mDataOutputStream;

    //连接服务器所需相关成员变量
    String urlStr=null;
    HttpURLConnection connection = null;
    OutputStream outputStream=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_audio_record);

//        //请求权限
//        RequestPermission requestPermission = new RequestPermission();
//        requestPermission.RequestPermission(this);

        initData();
//        initUI();

    }

    //初始化数据
    private void initData() {

        mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);//计算最小缓冲区
        mPlayBuffSize = AudioTrack.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);//播放所需最小缓冲区
        mAudioRecord = new AudioRecord(mAudioSource, mSampleRateInHz, mChannelConfig,
                mAudioFormat, mBufferSizeInBytes);//创建AudioRecorder对象
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRateInHz, mChannelConfig, mAudioFormat, mPlayBuffSize, AudioTrack.MODE_STREAM);
        mFileRoot = new File(mPathName);
        if (!mFileRoot.exists())
            mFileRoot.mkdirs();//创建文件夹
        setUpConnectionWithServer();
    }

    public void setUpConnectionWithServer(){
        try {
            urlStr="http://light.qingxu.website:16560/file/upload/audio";
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方法
            connection.setRequestMethod("POST");
            //设置连接超时时间（毫秒）
            connection.setConnectTimeout(5000);
            //设置读取超时时间（毫秒）
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            //设置缓冲大小，当流达到这个值后输出，参数为0则代表采用操作系统默认值
            connection.setChunkedStreamingMode(1024*1024);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

//    //初始化UI
//    private void initUI() {
//        mBtnStartRecord = findViewById(R.id.btn_start_record);
//        mBtnStopRecord = findViewById(R.id.btn_stop_record);
//        mBtnStartRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startRecord();
//            }
//        });
//
//        mBtnStopRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopRecord();
//            }
//        });
//    }

    //开始录音
    public void startRecord() {

        //AudioRecord.getMinBufferSize的参数是否支持当前的硬件设备
        if (AudioRecord.ERROR_BAD_VALUE == mBufferSizeInBytes || AudioRecord.ERROR == mBufferSizeInBytes) {
            throw new RuntimeException("Unable to getMinBufferSize");
        } else {
            destroyThread();
            isRecording = true;
            if (mThread == null) {
                mThread = new Thread(this);
                mThread.start();//开启线程
            }
        }
    }

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mThread && Thread.State.RUNNABLE == mThread.getState()) {
                try {
                    Thread.sleep(500);
                    mThread.interrupt();
                } catch (Exception e) {
                    mThread = null;
                }
            }
            mThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mThread = null;
        }
    }

    //停止录音
    public void stopRecord() {
        //停止录音，回收AudioRecord对象，释放内存
        if (mAudioRecord != null) {
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {//初始化成功
                mAudioRecord.stop();
            }
            if (mAudioRecord != null) {
                mAudioRecord.release();
            }
        }
        if (audioTrack != null) {
            if(audioTrack.getState()==AudioTrack.STATE_INITIALIZED) {
                audioTrack.stop();
            }
            audioTrack.release();
        }
        if(outputStream!=null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(mDataOutputStream!=null){
            try {
                mDataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        //标记为开始采集状态
        isRecording = true;

        //判断AudioRecord未初始化，停止录音的时候释放了，状态就为STATE_UNINITIALIZED
        if (mAudioRecord.getState() == mAudioRecord.STATE_UNINITIALIZED) {
            initData();
        }
        //创建一个流，存放从AudioRecord读取的数据
        mRecordingFile = new File(mFileRoot, mFileName);
        Log.i("yu", "文件路径：" + mFileRoot.getAbsolutePath());
        if (mRecordingFile.exists()) {//音频文件保存过了，删除
            mRecordingFile.delete();
        }
        try {
            mRecordingFile.createNewFile();//创建新文件
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("lu", "创建储存音频文件出错");
        }
        Log.i("yu", "准备录音啦~");
        try {
            //获取到文件的数据流
            mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mRecordingFile)));
            byte[] buffer = new byte[mBufferSizeInBytes];
            Log.i("yu", "开始录音啦~");
            //开始录音
            mAudioRecord.startRecording();
            audioTrack.play();
            Log.i("yu","准备给服务器发数据");

            try {
                outputStream = connection.getOutputStream();
            }
            catch (IOException e){
                Log.e("yu","给服务器发送数据失败");
                e.printStackTrace();
            }
            BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(outputStream);
            //getRecordingState获取当前AudioRecording是否正在采集数据的状态
            while(isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);//从MIC获取原始音频数据
                //把音频字节数据写入输出流

                for (int i = 0; i < bufferReadResult; i++) {
                   mDataOutputStream.write(buffer[i]);
                }

                Log.i("yu", String.valueOf(bufferReadResult));
                byte[] tmpBuffer = new byte[bufferReadResult];
                System.arraycopy(buffer, 0, tmpBuffer, 0, bufferReadResult);
                mDataOutputStream.write(tmpBuffer,0,bufferReadResult);
                mDataOutputStream.flush();
                bufferedOutputStream.write(tmpBuffer,0,tmpBuffer.length);
                bufferedOutputStream.flush();

                // 写入数据到AudioTrack播放
                audioTrack.write(tmpBuffer, 0, tmpBuffer.length);
            }
            bufferedOutputStream.close();
            outputStream.close();
            Log.i("yu","response code:"+connection.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("yu", "Recording Failed");
            stopRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyThread();
        stopRecord();
    }
}
