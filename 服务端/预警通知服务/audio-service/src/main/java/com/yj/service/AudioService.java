package com.yj.service;

import com.yj.vo.AudioFileInfoVo;
import com.yj.vo.JsonData;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface AudioService {
    void saveAudioFile(byte[] bytes,Long ward);
    void sendWarning(Long user_id,String content);
    //获取监护人对应的所有被监护人的录音记录
    List<AudioFileInfoVo>getRecordInfos(Long guardian);
    JsonData saveAudioNotice(Long ward, MultipartFile multipartFile, Date noticeTime);
    JsonData getNoticeInfos(Long ward);
}
