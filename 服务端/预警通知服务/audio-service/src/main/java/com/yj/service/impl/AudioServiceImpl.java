package com.yj.service.impl;

import com.yj.config.MinioConfig;
import com.yj.entity.Guard;
import com.yj.entity.WarningRecord;
import com.yj.entity.pk.WarningRecordPK;
import com.yj.repository.GuardRepository;
import com.yj.repository.WarningRecordRepository;
import com.yj.service.AccountService;
import com.yj.service.AudioService;
import com.yj.util.Pcm2Wav;
import com.yj.vo.AudioFileInfoVo;
import com.yj.vo.JsonData;
import com.yj.vo.NoticeInfoVo;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AudioServiceImpl implements AudioService {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    AmqpTemplate rabbitTemplate;
    @Autowired
    WarningRecordRepository warningRecordRepository;
    @Autowired
    GuardRepository guardRepository;
    @Autowired
    MinioClient minioClient;
    @Autowired
    MinioConfig minioConfig;
    @Autowired
    AccountService accountService;
    @Override
    public void saveAudioFile(byte[] bytes,Long ward) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd-HHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置为北京时间
        Date date = new Date(System.currentTimeMillis());
        //用上传文件的时间作为文件名
        String fileName = ward +File.separator+formatter.format(date)+".wav";
        try {
            InputStream is=Pcm2Wav.parse2Stream(bytes);
            //如果bucket不存在，则创建
            if (!minioClient.bucketExists(minioConfig.getBucketName())) {
                minioClient.makeBucket(minioConfig.getBucketName());
            }
            minioClient.putObject(minioConfig.getBucketName(),fileName,is,"audio/wav");
            sendWarning(ward,"录音预警");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendWarning(Long wardId,String content) {

        Date date = new Date();// 获取当前时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd-HHmmss",Locale.CHINA);
        String key="warning_"+ wardId+"_"+sdf.format(date);
        WarningRecordPK warningRecordPK = WarningRecordPK.builder().ward(wardId).time(date).build();
        WarningRecord warningRecord = WarningRecord.builder().warningRecordPK(warningRecordPK).content(content).build();
        warningRecordRepository.save(warningRecord);
        //将预警信息放入redis缓存
        redisTemplate.opsForValue().set(key,content);
        //过期时间设为一天
        redisTemplate.expire(key, 60L * 60*24, TimeUnit.SECONDS);
    }

    @Override
    public List<AudioFileInfoVo> getRecordInfos(Long guardian) {
        List<AudioFileInfoVo> audioFileInfoVos =new LinkedList<>();
        List<Guard> guards=guardRepository.findAllByGuardPKGuardian(guardian);
        for(Guard guard:guards){
            final Iterable<Result<Item>> results = minioClient.listObjects(
                    minioConfig.getBucketName(),//存储桶名称。
                    guard.getGuardPK().getWard() + File.separator,//对象名称的前缀，列出有该前缀的对象。
                    false);//是否递归查找，如果是false,就模拟文件夹结构查找。
            for(Result<Item> result:results){
                try {
                    Item item=result.get();
                    AudioFileInfoVo audioFileInfoVo=AudioFileInfoVo.builder()
                            .fileName(item.objectName())
                            .lastModifiedTime(item.lastModified())
                            .srcUrl(minioClient.presignedGetObject(minioConfig.getBucketName(), item.objectName()))
                            .build();
                    audioFileInfoVos.add(audioFileInfoVo);
                } catch (Exception e)  {
                    e.printStackTrace();
                }
            }
        }
        return audioFileInfoVos;
    }

    @Override
    public JsonData saveAudioNotice(Long ward, MultipartFile multipartFile, Date noticeTime) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置为北京时间
        //构造文件名
        String fileName = ward +File.separator+formatter.format(noticeTime)+".wav";
        try {
            //如果bucket不存在，则创建
            if (!minioClient.bucketExists("notice")) {
                minioClient.makeBucket(minioConfig.getBucketName());
            }
            minioClient.putObject(minioConfig.getBucketName(),fileName,multipartFile.getInputStream(),"audio/wav");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonData.builder()
                    .code(-1)
                    .msg("保存录音提醒失败")
                    .build();
        }
        return JsonData.builder()
                .code(0)
                .msg("保存录音提醒成功")
                .build();
    }

    @Override
    public JsonData getNoticeInfos(Long ward) {
        List<NoticeInfoVo> noticeInfoVos=new LinkedList<>();
        return null;
    }
}
