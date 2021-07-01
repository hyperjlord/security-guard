package com.yj.controller;

import com.yj.dto.AccountInfo;
import com.yj.entity.Setting;
import com.yj.entity.WarningRecord;
import com.yj.qo.SettingQo;
import com.yj.qo.UpdateSettingQo;
import com.yj.service.AccountService;
import com.yj.service.GuardianService;
import com.yj.vo.GuardianInfoVo;
import com.yj.vo.JsonData;
import com.yj.vo.WardInfoVo;
import com.yj.vo.WarningRecordVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/guardian")
public class GuardianController {
    @Autowired
    GuardianService guardianService;
    @Autowired
    AccountService accountService;
    @Autowired
    HttpServletRequest request;

    @ApiOperation(value = "关联被监护人")
    @PostMapping("/bind/{wardId}")
    void BindWard(@PathVariable("wardId") Long wardId){
        AccountInfo accountInfo=accountService.getAccountInfo("token");
        guardianService.bindWard(accountInfo.getId(),wardId);
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/userinfo")
    AccountInfo getAccountInfo(@RequestHeader("token")String token){
            return accountService.getAccountInfo(token);
    }

    @ApiOperation(value = "获取所有被监护人信息")
    @GetMapping("/list/ward")
    List<WardInfoVo> getWardList(@RequestHeader("token")String token){
        AccountInfo accountInfo=accountService.getAccountInfo(token);
        Long guardian=accountInfo.getId();
        return guardianService.getWardList(guardian);
    }

    @ApiOperation(value = "获取所有监护人信息")
    @GetMapping("/list/guardian")
    List<GuardianInfoVo> getGuardianList(@RequestHeader("token")String token){
        AccountInfo accountInfo=accountService.getAccountInfo(token);
        Long ward=accountInfo.getId();
        return guardianService.getGuardianList(ward);
    }

    @ApiOperation(value = "查看是否有预警信息（用于轮询）")
    @GetMapping("/check/warning")
    JsonData checkWarning(@RequestHeader("token") String token){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        JsonData jsonData = new JsonData();
        Boolean result=guardianService.checkWarningRecord(accountInfo.getId());
        if(result){
            jsonData.setMsg("有新的预警信息");
            jsonData.setCode(1);
        }else {
            jsonData.setMsg("暂无新的预警信息");
            jsonData.setCode(0);
        }
        return jsonData;
    }

    @ApiOperation(value = "获取所有对应被监护人的预警信息（按时间顺序排列）")
    @GetMapping("/list/warning")
    List<WarningRecordVo> getWarningRecords(@RequestHeader("token")String token){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        Long guardian=accountInfo.getId();
        return guardianService.getWarningRecordList(guardian);
    }

    @ApiOperation(value = "更新配置（目前还有问题，先别用）")
    @PostMapping("/update/setting")
    void updateSettings(@RequestHeader("token") String token,@RequestBody() SettingQo settingQo){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        Setting setting=Setting.builder()
                .ward(accountInfo.getId())
                .build();
        guardianService.updateSetting(setting);
    }

    @ApiOperation(value = "更新室内配置")
    @PostMapping("/indoor/setting")
    JsonData updateIndoorSetting(@RequestHeader("token") String token,@RequestBody() UpdateSettingQo updateSettingQo){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return guardianService.updateIndoorSetting(accountInfo.getId(),updateSettingQo);
    }

    @ApiOperation(value = "更新室外配置")
    @PostMapping("/outdoor/setting")
    JsonData updateOutdoorSetting(@RequestHeader("token") String token,@RequestBody() UpdateSettingQo updateSettingQo){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return guardianService.updateOutdoorSetting(accountInfo.getId(),updateSettingQo);
    }
    @ApiOperation(value = "获取室内配置")
    @GetMapping("/indoor/setting")
    JsonData getIndoorSetting(@RequestHeader("token") String token,@RequestParam("stamp") Long stamp){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return guardianService.getIndoorSetting(accountInfo.getId(),stamp);
    }
    @ApiOperation(value = "获取室外配置")
    @GetMapping("/outdoor/setting")
    JsonData getOutdoorSetting(@RequestHeader("token") String token,@RequestParam("stamp") Long stamp){
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        return guardianService.getOutdoorSetting(accountInfo.getId(),stamp);
    }

}
