package com.yj.service;

import com.yj.entity.Setting;
import com.yj.entity.WarningRecord;
import com.yj.qo.SettingQo;
import com.yj.qo.UpdateSettingQo;
import com.yj.vo.GuardianInfoVo;
import com.yj.vo.JsonData;
import com.yj.vo.WardInfoVo;
import com.yj.vo.WarningRecordVo;

import java.util.List;

public interface GuardianService {
    void bindWard(Long guardianId,Long wardId);
    List<WardInfoVo> getWardList(Long guardianId);
    List<GuardianInfoVo>getGuardianList(Long wardId);
    List<WarningRecordVo>getWarningRecordList(Long guardianId);
    Boolean checkWarningRecord(Long guardianId);
    void updateSetting(Setting setting);
    JsonData updateIndoorSetting(Long guardian,UpdateSettingQo updateSettingQo);
    JsonData updateOutdoorSetting(Long guardian,UpdateSettingQo updateSettingQo);
    JsonData getIndoorSetting(Long ward,Long stamp);
    JsonData getOutdoorSetting(Long ward,Long stamp);
}
