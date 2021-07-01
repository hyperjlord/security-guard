package com.example.share.thread.UpdateThread;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

public class timeUpdate implements Runnable{


    public TextView date;
    public TextView time;
    String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public timeUpdate(TextView Date,TextView Time)
    {
        date=Date;
        time=Time;
    }

    @Override
    public void run() {
        while (true) {
            try {

                Calendar cal = Calendar.getInstance();
                int m = cal.get(Calendar.MONTH)+1;
                int d = cal.get(Calendar.DATE);
                int h = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);
                time.setText(h + ":" + min);
                int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
                if (w < 0)
                    w = 0;
                date.setText(m + "月" + d + "日 " + weekDays[w]);
                Thread.sleep(60000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
