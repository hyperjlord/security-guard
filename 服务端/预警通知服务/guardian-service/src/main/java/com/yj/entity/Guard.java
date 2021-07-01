package com.yj.entity;

import com.yj.entity.pk.GuardPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Guard {
    @EmbeddedId
    GuardPK guardPK;
    String guardianName;
    String wardName;
}
