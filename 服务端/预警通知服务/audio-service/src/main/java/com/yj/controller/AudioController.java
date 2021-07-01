package com.yj.controller;

import com.yj.dto.AccountInfo;
import com.yj.service.AccountService;
import com.yj.service.AudioService;
import com.yj.util.OSInfo;
import com.yj.util.Pcm2Wav;
import com.yj.vo.AudioFileInfoVo;
import com.yj.vo.JsonData;
import com.yj.vo.NoticeInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
public class AudioController {
    @Autowired
    AudioService audioService;
    @Autowired
    AccountService accountService;

    @ApiOperation(
            value = "上传录音文件",
            notes ="用于一键录音功能上传文件"
    )
    @PostMapping("/file/upload/audio")
    void acceptAudioStream(@RequestBody byte[] recordingBytes, @RequestHeader("token") String token){
        AccountInfo accountInfo=accountService.getAccountInfo(token);
        Long ward=accountInfo.getId();
        audioService.saveAudioFile(recordingBytes,ward);
    }

    @ApiOperation(
            value = "安全边界预警",
            notes="接收前端安全边界预警，再向监护人端发送预警"
    )
    @GetMapping("/warning/boundary")
    void sendBoundaryWarning(@RequestHeader("token")String token){
        AccountInfo accountInfo=accountService.getAccountInfo(token);
        Long user_id=accountInfo.getId();
        audioService.sendWarning(user_id,"安全边界预警");
    }

    @ApiOperation(
            value = "室内异常预警",
            notes="接收前端室内异常预警，再向监护人端发送预警"
    )
    @GetMapping("/warning/indoor")
    void sendIndoorWarning(@RequestHeader("token")String token){
        AccountInfo accountInfo=accountService.getAccountInfo(token);
        Long user_id=accountInfo.getId();
        audioService.sendWarning(user_id,"室内异常预警");
    }

    @ApiOperation(
            value = "获取录音文件",
            notes="监护人端发现预警后可调用此接口获取录音文件"
    )
    @GetMapping("/file/download/audio")
    ResponseEntity<InputStreamResource> downloadFile(@RequestHeader("token") String token) throws IOException {
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        String filePath = OSInfo.getStorageDir()+ accountInfo.getId()+".pcm";
        String targetPath = OSInfo.getStorageDir()+ accountInfo.getId()+".wav";
        try {
            Pcm2Wav.parse(filePath,targetPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileSystemResource file = new FileSystemResource(targetPath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }

    @ApiOperation(
            value = "获取被监护人的录音文件信息",
            notes="监护人获取其被监护人的录音文件信息"
    )
    @GetMapping("/file/infos/audio")
    List<AudioFileInfoVo> getAudioFileInfos(@RequestHeader("token") String token){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return audioService.getRecordInfos(accountInfo.getId());
    }

    @PostMapping("/update/notice")
    JsonData saveAudioNotice(@RequestHeader("token") String token,
                         @RequestParam("noticeTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date noticeTime,
                         @RequestParam("noticeFile") MultipartFile multipartFile){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return audioService.saveAudioNotice(accountInfo.getId(),multipartFile,noticeTime);
    }

    @GetMapping("/list/notice")
    JsonData getNoticeInfos(@RequestHeader("token") String token){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return null;
    }
}
