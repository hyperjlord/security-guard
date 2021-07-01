package website.qingxu.homesecure.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import website.qingxu.homesecure.qo.*;
import website.qingxu.homesecure.service.AccountService;
import website.qingxu.homesecure.service.CameraService;
import website.qingxu.homesecure.utils.CameraUtils;
import website.qingxu.homesecure.vo.*;
import website.qingxu.homesecure.entity.*;
import website.qingxu.homesecure.dao.CameraRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v0.0.1/camera")
@Api(value = "摄像头相关接口")
public class CameraController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private CameraService cameraService;
    @Autowired
    private CameraRepository cameraRepository;

    @PutMapping(value = "/add")
    @ResponseBody
    public CameraAddVO add(
            @RequestHeader("token") String token,
            @RequestBody CameraAddQO request
    ) {
        CameraAddVO res = new CameraAddVO();
        if (request.getCameraName().length() > 50){
            res.error("摄像头名称过长，请保持在50个中文字符内");
            return res;
        }
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        List<Camera> cameraList = cameraRepository.findAllByUserId(accountInfo.getId());
        //检查是否重复添加
        for (Camera camera : cameraList) {
            if (camera.getCameraSn().equals(request.getCameraSn())) {
                camera.setCameraName(request.getCameraName());
                cameraRepository.save(camera);
                res.success("已添加曾使用过的摄像头，不消耗可用摄像头数量");
                res.setCameraId(camera.getCameraId());
                return res;
            }
        }
        //检查账户摄像头数量上限
        if (cameraList.size() >= CameraUtils.getCameraLimit(accountInfo.getAccountType())) {
            res.error("已达账户可用摄像头数量上限，订阅会员可解锁上限");
            return res;
        }
        //添加新摄像头
        Camera camera = new Camera();
        camera.setCameraName(request.getCameraName());
        camera.setUserId(accountInfo.getId());
        camera.setCameraSn(request.getCameraSn());
        camera.setStreamPass(CameraUtils.genInitialPass(10));
        camera.setStreamRoom("live");
        cameraRepository.save(camera);
        res.success(String.format("新增名为 %s 的摄像头到账户名下", request.getCameraName()));
        res.setCameraId(camera.getCameraId());
        return res;
    }

    @DeleteMapping(value = "/remove")
    @ResponseBody
    public Result remove(
            @RequestHeader("token") String token,
            @RequestBody CameraRemoveQO request
    ) {
        Result res = new Result();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        Camera camera;
        Optional<Camera> optionalCamera = cameraRepository.findById(request.getCameraId());
        if (!optionalCamera.isPresent()){
            res.error("未找到目标摄像头，请刷新后重试");
            return res;
        }
        camera = optionalCamera.get();
        if(camera.getUserId() != accountInfo.getId()){
            res.error("该账号对摄像头没有访问权限，请换用摄像头绑定的账户登录");
            return res;
        }
        cameraRepository.delete(camera);
        res.success(String.format("成功移除账户名下 %s 摄像头，可用摄像头数量+1", camera.getCameraName()));
        return res;
    }

    @PostMapping(value = "/update")
    @ResponseBody
    public Result update(
            @RequestHeader("token") String token,
            @RequestBody CameraUpdateQO request
    ){
        Result res = new Result();
        if (request.getCameraName().length() > 50){
            res.error("摄像头名称过长，请保持在50个中文字符内");
            return res;
        }
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        Camera camera;
        Optional<Camera> optionalCamera = cameraRepository.findById(request.getCameraId());
        if (!optionalCamera.isPresent()){
            res.error("未找到目标摄像头，请刷新");
            return res;
        }
        camera = optionalCamera.get();
        if(camera.getUserId() != accountInfo.getId()){
            res.error("该账号对摄像头没有访问权限，请换用摄像头绑定的账户登录");
            return res;
        }
        camera.setCameraName(request.getCameraName());
        cameraRepository.save(camera);
        res.success(String.format("成功修改摄像头名为：%s", camera.getCameraName()));
        return res;
    }

    @GetMapping(value = "/my-list")
    @ResponseBody
    public CameraListVO getMyCameraList(
            @RequestHeader("token") String token
    ){
        CameraListVO res = new CameraListVO();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        res.setList(this.cameraService.getCameraList(accountInfo.getId()));
        res.success("成功获取账户名下摄像头");
        return res;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public AccountCameraListVO getAccountCameraList(
            @RequestHeader("token") String token
    ){
        AccountCameraListVO res = new AccountCameraListVO();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        res.setAccountCameraList(this.cameraService.getAccountCameraList(accountInfo));
        res.success("成功获取账户名下摄像头");
        return res;
    }

    @GetMapping(value = "/ward-list")
    @ResponseBody
    public CameraListVO getWardCameraList(
            @RequestHeader("token") String token,
            @RequestParam long wardId
    ){
        CameraListVO res = new CameraListVO();
        boolean isGuardian = false;
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        for(GuardInfo guardInfo : accountInfo.getWards()){
            if(guardInfo.getAccountId() == wardId){
                isGuardian = true;
            }
        }
        if(!isGuardian){
            res.error("请先绑定监护关系再访问");
            return res;
        }

        res.setList(this.cameraService.getCameraList(wardId));
        res.success("成功获取被监护人名下摄像头");
        return res;
    }
}
