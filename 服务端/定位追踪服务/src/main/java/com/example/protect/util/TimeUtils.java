package com.example.protect.util;


import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TimeUtils {



    public int timeTransfer(String timeString)
    {
        String[] timeArray = timeString.split(":");
        int res=0;
        res+=Integer.parseInt(timeArray[0])*3600;
        res+=Integer.parseInt(timeArray[1])*60;
        res+=Integer.parseInt(timeArray[2]);
        return res;
    }
    /**
     * 获取精确到秒的时间戳
     * @param date
     * @return
     */
    public long getSecondTimestampTwo(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime()/1000);
        return Long.parseLong(timestamp);
    }
}
