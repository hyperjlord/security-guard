package com.example.share.service.mapService.BoundaryUpdate;

import java.util.HashSet;
import java.util.Set;

public class RouteSet {
    public RouteSet() {
        route_set = new HashSet<>();
    }
    public Set<Route> route_set; //路径队列集合

    //得到所有路径中的四个极端坐标
    public double[] get4ExtremeValue() {
        double north = -91, south = 91, east = -181, west = 181;
        for (Route route : route_set) {
            double[] candidate = route.get4ExtremeValue();
            if (candidate[0] > north) north = candidate[0];
            if (candidate[1] < south) south = candidate[1];
            if (candidate[2] > east) east = candidate[2];
            if (candidate[3] < west) west = candidate[3];
        }
        double[] result = new double[]{north, south, east, west};
        return result;
    }

    public void addRoute(Route route) {
        route_set.add(route);
    }

}
