package com.yj.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonData {
    //状态码 0 表示成功，1表示处理中，-1表示失败
    Integer code;
    //处理信息
    String msg;
    //数据
    Object data;
}
