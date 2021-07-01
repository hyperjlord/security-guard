package website.qingxu.homesecure.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import website.qingxu.homesecure.dao.CameraRepository;
import website.qingxu.homesecure.entity.*;
import website.qingxu.homesecure.qo.StreamSuccessQO;
import website.qingxu.homesecure.qo.UpdatePassQO;
import website.qingxu.homesecure.service.*;
import website.qingxu.homesecure.utils.*;
import website.qingxu.homesecure.vo.*;

import java.util.Optional;

@RestController
@RequestMapping("/v0.0.1/stream")
@Api(value = "串流接口")
public class StreamController {
    private static final int EXPIRE_TIME = 5 * 60;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CameraRepository cameraRepository;

    private static final String NO_ACCESS_MSG = "该账号对摄像头没有访问权限，请换用摄像头绑定的账户登录";

    @GetMapping(value = "/launch-stream")
    @ResponseBody
    public StreamReadyVO launchStream(
            @RequestHeader("token") String token,
            @RequestParam long wardId,
            @RequestParam long cameraId
    ) {
        StreamReadyVO res = new StreamReadyVO();
        boolean isGuardian = false;
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        for (GuardInfo guardInfo : accountInfo.getWards()) {
            if (guardInfo.getAccountId() == wardId) {
                isGuardian = true;
                break;
            }
        }
        if (!isGuardian) {
            res.error("请先绑定监护关系再访问");
            return res;
        }
        Camera camera;
        Optional<Camera> optionalCamera = cameraRepository.findById(cameraId);
        if (!optionalCamera.isPresent()) {
            res.error("未找到目标摄像头，请刷新后重试");
            return res;
        }
        camera = optionalCamera.get();
        if (camera.getUserId() != wardId) {
            res.error("被监护人对摄像头没有访问权限，请换用摄像头绑定的账户访问");
            return res;
        }
        StreamStatus status;
        //检查是否已经在串流
        status = (StreamStatus) redisUtils.get(CameraUtils.transToStreamStatusKey(cameraId));
        if (status != null) {
            redisUtils.expire(CameraUtils.transToStreamStatusKey(cameraId), EXPIRE_TIME);
            res.setRTMPurl(status.getRTMPurl());
            res.setHTTPurl(status.getHTTPurl());
            if(!status.isSuccess()){
                res.waiting("等待被监护人端响应");
                return res;
            }
            res.success(String.format("串流地址为：%s", res.getRTMPurl()));
            return res;
        }
        //请求启动串流
        status = new StreamStatus();
        status.setRTMPurl(CameraUtils.transToRTMPUrl(wardId, camera.getStreamPass(), camera.getStreamRoom()));
        status.setHTTPurl(CameraUtils.transToHTTPUrl(wardId, camera.getStreamPass(), camera.getStreamRoom()));
        status.setWardId(wardId);
        if (!redisUtils.set(CameraUtils.transToStreamStatusKey(cameraId), status, EXPIRE_TIME)) {
            res.warning("服务器内部网络问题，请稍后再试");
            return res;
        }
        res.setRTMPurl(status.getRTMPurl());
        res.setHTTPurl(status.getHTTPurl());
        res.waiting("等待被监护人端响应");
        return res;
    }

    @GetMapping(value = "/camera-heartbeat")
    @ResponseBody
    public HeartBeatVO cameraHeartBeat(
            @RequestHeader("token") String token,
            @RequestParam long cameraId
    ) {
        HeartBeatVO res = new HeartBeatVO();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        StreamStatus status = (StreamStatus) redisUtils.get(CameraUtils.transToStreamStatusKey(cameraId));
        if (status == null) {
            res.waiting("暂无新指令");
            return res;
        }
        if (status.getWardId() != accountInfo.getId()) {
            res.error(NO_ACCESS_MSG);
            return res;
        }
        Command command = new Command();
        command.setCode(Command.CommandCode.KEEP_STREAM);
        JSONObject params = new JSONObject();
        params.put("RTMPurl", status.getRTMPurl());
        command.setParams(params);
        res.setCommand(command);
        res.success("启动串流");
        return res;
    }

    @PostMapping(value = "/pass")
    @ResponseBody
    public Result updatePass(
            @RequestHeader("token") String token,
            @RequestBody UpdatePassQO request
    ) {
        Result res = new Result();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        Camera camera;
        Optional<Camera> optionalCamera = cameraRepository.findById(request.getCameraId());
        if(!optionalCamera.isPresent()){
            return res;
        }
        camera = optionalCamera.get();
        if (camera.getUserId() != accountInfo.getId()) {
            res.error(NO_ACCESS_MSG);
            return res;
        }
        camera.setStreamPass(CameraUtils.genInitialPass(10));
        cameraRepository.save(camera);
        res.success("成功修改串流密钥");
        return res;
    }

    @PutMapping(value = "/stream-success")
    @ResponseBody
    public Result streamSuccess(
            @RequestHeader("token") String token,
            @RequestBody StreamSuccessQO request
    ) {
        Result res = new Result();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        Camera camera;
        Optional<Camera> optionalCamera = cameraRepository.findById(request.getCameraId());
        if (!optionalCamera.isPresent()) {
            res.error("未找到目标摄像头，请刷新后重试");
            return res;
        }
        camera = optionalCamera.get();
        if (camera.getUserId() != accountInfo.getId()) {
            res.error(NO_ACCESS_MSG);
            return res;
        }
        StreamStatus status = (StreamStatus) redisUtils.get(CameraUtils.transToStreamStatusKey(request.getCameraId()));
        if(status == null){
            res.error("响应时间超时，监护人端已下线");
            return res;
        }
        status.setSuccess(true);
        if(!redisUtils.set(CameraUtils.transToStreamStatusKey(request.getCameraId()), status, EXPIRE_TIME)){
            res.warning("服务器内部网络错误，请重试");
            return res;
        }
        res.success("已通知监护人端");
        return res;
    }

    @GetMapping(value = "/stream-heartbeat")
    @ResponseBody
    public HeartBeatVO streamHeartBeat(
            @RequestHeader("token") String token,
            @RequestParam long cameraId
    ) {
        HeartBeatVO res = new HeartBeatVO();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        if (accountInfo == null){
            res.error("请重新登录");
            return res;
        }
        StreamStatus status = (StreamStatus) redisUtils.get(CameraUtils.transToStreamStatusKey(cameraId));
        //启动超时
        if (status == null) {
            res.error("摄像头串流启动失败，请确认被监护端应用是否正常运行");
            return res;
        }
        //是否就绪
        if(!status.isSuccess()){
            res.waiting("摄像头串流未就绪，请等待");
            return res;
        }
        //检查监护关系
        boolean isGuardian = false;
        for(GuardInfo guardInfo: accountInfo.getWards()){
            if (status.getWardId() == guardInfo.getAccountId()) {
                isGuardian = true;
                break;
            }
        }
        if (!isGuardian) {
            res.error("该账号对摄像头没有访问权限，请确认监护关系");
            return res;
        }
        //更新过期时间
        if(!redisUtils.expire(CameraUtils.transToStreamStatusKey(cameraId), EXPIRE_TIME)){
            res.warning("服务器内部网络错误，请稍后再试");
            return res;
        }
        Command command = new Command();
        command.setCode(Command.CommandCode.PULL_STREAM);
        res.setCommand(command);
        res.success("串流就绪");
        return res;
    }
}
