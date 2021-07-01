package com.yj.service.impl;

import com.yj.entity.Account;
import com.yj.entity.Guard;
import com.yj.entity.Setting;
import com.yj.entity.WarningRecord;
import com.yj.entity.pk.GuardPK;
import com.yj.qo.SettingQo;
import com.yj.qo.UpdateSettingQo;
import com.yj.repository.AccountRepository;
import com.yj.repository.GuardRepository;
import com.yj.repository.SettingRepository;
import com.yj.repository.WarningRecordRepository;
import com.yj.service.GuardianService;
import com.yj.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class GuardianServiceImpl implements GuardianService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    GuardRepository guardRepository;
    @Autowired
    WarningRecordRepository warningRecordRepository;
    @Autowired
    SettingRepository settingRepository;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public void bindWard(Long guardianId, Long wardId) {
        GuardPK guardPK = GuardPK.builder().guardian(guardianId).ward(wardId).build();
        Guard guard = Guard.builder().guardPK(guardPK).guardianName("张三").wardName("李四").build();
        guardRepository.save(guard);
    }

    @Override
    public List<WardInfoVo> getWardList(Long guardianId) {
        List<WardInfoVo> wardInfoVos = new LinkedList<>();
        for (Guard guard : guardRepository.findAllByGuardPKGuardian(guardianId)) {
            wardInfoVos.add(WardInfoVo.builder()
                    .ward(guard.getGuardPK().getWard())
                    .wardName(guard.getWardName())
                    .build());
        }
        return wardInfoVos;
    }

    @Override
    public List<GuardianInfoVo> getGuardianList(Long wardId) {
        List<GuardianInfoVo> guardianInfoVos = new LinkedList<>();
        for (Guard guard : guardRepository.findAllByGuardPKWard(wardId)) {
            Account account = accountRepository.findById(guard.getGuardPK().getGuardian()).orElse(null);
            guardianInfoVos.add(GuardianInfoVo.builder()
                    .guardian(guard.getGuardPK().getGuardian())
                    .guardianName(guard.getGuardianName())
                    .phone(account.getPhone())
                    .build());
        }
        return guardianInfoVos;
    }

    @Override
    public List<WarningRecordVo> getWarningRecordList(Long guardian) {
        List<Guard> guards = guardRepository.findAllByGuardPKGuardian(guardian);
        List<Long> wards = new ArrayList<>();
        for (Guard guard : guards) {
            wards.add(guard.getGuardPK().getWard());
        }
        List<WarningRecord> warningRecords = warningRecordRepository
                .findAllByWarningRecordPKWardInOrderByWarningRecordPKTimeDesc(wards);
        List<WarningRecordVo> warningRecordVos = new LinkedList<>();
        for (WarningRecord warningRecord : warningRecords) {
            warningRecordVos.add(WarningRecordVo.builder()
                    .ward(warningRecord.getWarningRecordPK().getWard())
                    .time(warningRecord.getWarningRecordPK().getTime())
                    .content(warningRecord.getContent())
                    .build());
        }
        return warningRecordVos;
    }

    @Override
    public Boolean checkWarningRecord(Long guardianId) {
        //根据guardianId查找所有guard关系
        List<Guard> guards = guardRepository.findAllByGuardPKGuardian(guardianId);
        //查询所有被监护人是否有预警信息
        for (Guard guard : guards) {
            Long wardId = guard.getGuardPK().getWard();
            //模糊匹配
            Set<String> keys = redisTemplate.keys("warning_" + wardId + "_*");
            //只要其中有一个监护人有预警消息，就返回true
            if(keys.size()>0){
                for (String key : keys) {
                    String content = (String) redisTemplate.opsForValue().get(key);
                    redisTemplate.delete(key);
                }
                return true;
            }
        }
        //如果所有被监护人都没有预警消息，返回false
        return false;
    }

    @Override
    public void updateSetting(Setting setting) {
        settingRepository.save(setting);
    }

    @Override
    public JsonData updateIndoorSetting(Long guardian, UpdateSettingQo updateSettingQo) {
        Long ward=updateSettingQo.getWard();
        Guard guard=guardRepository.findById(GuardPK.builder().guardian(guardian).ward(ward).build()).orElse(null);
        //如果guard为空说明监护人没有关联被监护人，无权配置
        if(guard==null){
            return JsonData.builder()
                    .code(-1)
                    .msg("监护人没有关联被监护人，无权配置")
                    .build();
        }
        Setting setting = settingRepository.findById(ward).orElse(null);
        //第一次配置
        if (setting == null) {
            setting = Setting.builder()
                    .ward(ward)
                    .indoorStamp(new Date().getTime())
                    .indoorSetting(updateSettingQo.getSetting())
                    .build();
        } else {//非第一次配置
            setting.setIndoorStamp(updateSettingQo.getTimeStamp());
            setting.setIndoorSetting(updateSettingQo.getSetting());
        }
        settingRepository.save(setting);
        return JsonData.builder()
                .code(0)
                .msg("更新配置成功")
                .build();
    }

    @Override
    public JsonData updateOutdoorSetting(Long guardian, UpdateSettingQo updateSettingQo) {
        Long ward=updateSettingQo.getWard();
        Guard guard=guardRepository.findById(GuardPK.builder().guardian(guardian).ward(ward).build()).orElse(null);
        //如果guard为空说明监护人没有关联被监护人，无权配置
        if(guard==null){
            return JsonData.builder()
                    .code(-1)
                    .msg("监护人没有关联被监护人，无权配置")
                    .build();
        }
        Setting setting = settingRepository.findById(ward).orElse(null);
        //第一次配置
        if (setting == null) {
            setting = Setting.builder()
                    .ward(ward)
                    .outdoorStamp(new Date().getTime())
                    .outdoorSetting(updateSettingQo.getSetting())
                    .build();
        } else {//非第一次配置
            setting.setOutdoorStamp(updateSettingQo.getTimeStamp());
            setting.setOutdoorSetting(updateSettingQo.getSetting());
        }
        settingRepository.save(setting);
        return JsonData.builder()
                .code(0)
                .msg("更新配置成功")
                .build();
    }

    @Override
    public JsonData getIndoorSetting(Long ward, Long stamp) {
        Setting setting = settingRepository.findById(ward).orElse(null);
        if (setting == null) {
            return JsonData.builder()
                    .code(-1)
                    .msg("服务器获取配置信息失败")
                    .build();
        }
        JsonData jsonData = new JsonData();
        //如果数据库有新配置项
        if (stamp < setting.getIndoorStamp()) {
            SettingVo settingVo = SettingVo.builder()
                    .timeStamp(setting.getIndoorStamp())
                    .setting(setting.getIndoorSetting())
                    .build();
            jsonData.setCode(0);
            jsonData.setData(settingVo);
            jsonData.setMsg("有新的配置");
        } else {
            jsonData.setCode(0);
            jsonData.setMsg("暂无新的配置");
        }
        return jsonData;
    }

    @Override
    public JsonData getOutdoorSetting(Long ward, Long stamp) {
        Setting setting = settingRepository.findById(ward).orElse(null);
        if (setting == null) {
            return JsonData.builder()
                    .code(-1)
                    .msg("服务器获取配置信息失败")
                    .build();
        }
        JsonData jsonData = new JsonData();
        //如果数据库有新配置项
        if (stamp < setting.getIndoorStamp()) {
            SettingVo settingVo = SettingVo.builder()
                    .timeStamp(setting.getOutdoorStamp())
                    .setting(setting.getOutdoorSetting())
                    .build();
            jsonData.setCode(0);
            jsonData.setData(settingVo);
            jsonData.setMsg("有新的配置");
        } else {
            jsonData.setCode(0);
            jsonData.setMsg("暂无新的配置");
        }
        return jsonData;
    }


}
