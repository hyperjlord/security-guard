package com.yj.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WardInfoVo {
    //被监护人id
    Long ward;
    //被监护人的称呼。eg.儿子
    String wardName;
}
