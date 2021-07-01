package com.example.share.service.mapService.BoundaryUpdate;

import java.util.HashSet;
import java.util.Set;

public class Boundary {
    public Boundary() {
        boundary = new HashSet<>();
    }
    public Set<Point> boundary; //存储坐标点的集合

    //取得边界的四个极限坐标
    public double[] get4ExtremeValue() {
        double north = -91, south = 91, east = -181, west = 181;
        for (Point point : boundary) {
            if (point.latitude > north) north = point.latitude;
            if (point.latitude < south) south = point.latitude;
            if (point.longitude > east) east = point.longitude;
            if (point.longitude < west) west = point.longitude;
        }
        double[] result = new double[]{north, south, east, west};
        return result;
    }

    public void addPoint(Point point){
        boundary.add(point);
    }

    public void deletePoint(double lat, double lng){
        if(isInBound(lat, lng)) {
            boundary.remove(search(lat, lng));
        }
        else System.out.println("the point isn't in bound!");
    }

    public Point search(double lat, double lng) {
        for (Point safe_point : boundary) {
            if (lat == safe_point.latitude && lng == safe_point.longitude)
                return safe_point;
        }
        return null;
    }

    public boolean isInBound(Point point) {
        for (Point safe_point : boundary) {
            if (point.latitude == safe_point.latitude && point.longitude == safe_point.longitude) return true;
        }
        return false;
    }

    public boolean isInBound(double lat, double lng) {
        for (Point safe_point : boundary) {
            if (lat == safe_point.latitude && lng == safe_point.longitude) return true;
        }
        return false;
    }

    public void frequent2zero(Point point) {
        for (Point safe_point : boundary) {
            if (point.latitude == safe_point.latitude && point.longitude == safe_point.longitude) {
                safe_point.untraveled_weeks = 0;
            }
        }
    }

    public void showBoundary(){
        for (Point point : boundary) {
            System.out.print(point.latitude);
            System.out.print(" ");
            System.out.print(point.longitude);
            System.out.print(" ");
            System.out.print(point.value);
            System.out.print(" ");
            System.out.print(point.untraveled_weeks);
            System.out.print('\n');
        }
    }

}
