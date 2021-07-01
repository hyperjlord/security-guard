package com.yj.entity;

import com.yj.entity.pk.WarningRecordPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warning_record")
public class WarningRecord {
    @EmbeddedId
    WarningRecordPK warningRecordPK;
    String content;
}
