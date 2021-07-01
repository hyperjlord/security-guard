package com.yj.entity;

import com.yj.entity.pk.GuardPK;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@Entity
public class Guard {
    @EmbeddedId
    GuardPK guardPK;
    String guardianName;
    String wardName;
}
