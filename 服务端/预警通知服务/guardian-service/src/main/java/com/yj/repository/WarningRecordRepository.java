package com.yj.repository;

import com.yj.entity.WarningRecord;
import com.yj.entity.pk.WarningRecordPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarningRecordRepository extends JpaRepository<WarningRecord, WarningRecordPK> {
    List<WarningRecord> findAllByWarningRecordPKWardInOrderByWarningRecordPKTimeDesc(List<Long> wards);
}
