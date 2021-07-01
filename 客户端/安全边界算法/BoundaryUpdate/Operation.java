package com.example.share.service.mapService.BoundaryUpdate;

import java.math.BigDecimal;

public class Operation {

    //高精度double+
    public static double sum(double A, double B) {
        BigDecimal x = new BigDecimal(Double.toString(A));
        BigDecimal y = new BigDecimal(Double.toString(B));
        return x.add(y).doubleValue();
    }

    //高精度double-
    public static double sub(double A,double B){
        BigDecimal x = new BigDecimal(Double.toString(A));
        BigDecimal y = new BigDecimal(Double.toString(B));
        return x.subtract(y).doubleValue();
    }

    //权值衰减函数
    public static double decrease(Point point, double S){
        /**
         * point: 坐标点
         * S: 记忆参数*/
        double ratio;
        if (point.untraveled_weeks == 0) return 1;
        else {
            double C1 = Math.pow(Math.E, (-1*(point.untraveled_weeks-1)/S));
            double C2 = Math.pow(Math.E, (-1*point.untraveled_weeks)/S);
            ratio = C2/C1;
        }
        return ratio;
    }

}
