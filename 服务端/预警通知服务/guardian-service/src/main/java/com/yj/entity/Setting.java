package com.yj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "setting")
public class Setting {
    @Id
    Long ward;
    Long indoorStamp;
    String indoorSetting;
    Long outdoorStamp;
    String outdoorSetting;
}
