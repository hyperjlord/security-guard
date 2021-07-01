package com.example.share.service.mapService.BoundaryUpdate;

public class Point {

    public double latitude; //纬度
    public double longitude; //经度
    public double value; //该点的权值
    int untraveled_weeks; //未经过的周数

    // 构造函数1
    public Point() {
        latitude = 0;
        longitude = 0;
        value = 0;
        untraveled_weeks = 0;
    }

    // 构造函数2
    public Point(double lat, double lng, double prob, int weeks) {
        latitude = lat;
        longitude = lng;
        value = prob;
        untraveled_weeks = weeks;
    }

    public void printLatLng(){
        System.out.print(this.latitude);
        System.out.print(" ");
        System.out.print(this.longitude);
        System.out.print('\n');
    }

}
