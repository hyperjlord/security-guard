package com.example.share.service.mapService.BoundaryUpdate;

import android.util.Log;

public class Initiate {

    public BoundBox bound_box; //测试方便临时设为public
    public Boundary boundary;
    public Boundary enclosure;
    RouteSet routes;
    int radius = 10; // 权值扩散半径
    double S = 0.95; // 记忆参数
    double U = 2; // 强度参数
    double initial_prob = 0.5; // 初始可能性
    double k = 0.5; // 扩散系数
    double min = 0.15; // 最低阈值

    public Initiate(RouteSet route_set, int level) {

        adjustParam(level);

        boundary = new Boundary();
        enclosure = new Boundary();
        routes = route_set;
        Log.e("运行中","start");
        double[] realms = routes.get4ExtremeValue();
        bound_box = new BoundBox(realms[0], realms[1], realms[2], realms[3], radius);
        Log.e("运行中","start1");
        bound_box.trackValues(boundary, routes, radius, U, initial_prob, k);
        Log.e("运行中","start2");
        bound_box.reap(boundary, min, radius);
        Log.e("运行中","start3");
        bound_box.setEnclosure(enclosure, radius);
        Log.e("运行中","start4");
    }

    public Boundary getResult() {
        return boundary;
    }

    public Boundary getEnclosure() {return enclosure;}

    private void adjustParam(int level) {
        switch (level){
            case 1: {
                radius = 10;
                break;
            }
            case 2: {
                radius = 15;
                break;
            }
            case 3: {
                radius = 20;
                break;
            }
        }
    }

}
