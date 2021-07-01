package com.yj.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardianInfoVo {
    Long guardian;
    String guardianName;
    String phone;
}
