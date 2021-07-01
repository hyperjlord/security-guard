package com.yj.util;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

public class Pcm2Wav {
    // sampleRate - 每秒的样本数
    // sampleSizeInBits - 每个样本中的位数
    // channels - 声道数（单声道 1 个，立体声 2 个）
    static float sampleRate = 8000;
    static int sampleSizeInBits = 16;
    static int channels = 2;
    static boolean signed = true;
    static boolean bigEndian = false;
    public static void parse(String source, String target) throws Exception {
        AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        File sourceFile = new File(source);
        FileOutputStream out = new FileOutputStream(new File(target));
        AudioInputStream audioInputStream = new AudioInputStream(new FileInputStream(sourceFile), af, sourceFile.length());
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
        audioInputStream.close();
        out.flush();
        out.close();
    }
    public static InputStream parse2Stream(byte[] source) throws Exception {
        //AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        //AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(source), af, source.length);
        WaveHeader header = new WaveHeader();
        //长度字段 = 内容的大小（PCMSize) + 头部字段的大小(不包括前面4字节的标识符RIFF以及fileLength本身的4字节)
        header.fileLength = source.length + (44 - 8);
        header.FmtHdrLength = 16;
        header.BitsPerSample = (short) sampleSizeInBits;
        header.Channels = 2;
        header.FormatTag = 0x0001;
        header.SamplesPerSec = (int)sampleRate;
        header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
        header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
        header.DataHdrLeth = source.length;
        byte[] h = header.getHeader();
        int totalLength=h.length+source.length;
        byte[] f=new byte[totalLength];
        assert h.length == 44; //WAV标准，头部应该是44字节
        System.arraycopy(h,0,f,0,h.length);
        System.arraycopy(source,0,f,h.length,source.length);
        InputStream inputStream=new ByteArrayInputStream(f);
        return  inputStream;
    }
}
