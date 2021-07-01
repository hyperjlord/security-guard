package com.example.share.service.mapService.BoundaryUpdate;

public class Update {

    public BoundBox bound_box; //测试方便临时设为public
    public Boundary boundary;
    public Boundary enclosure;
    RouteSet routes;
    int radius = 3; // 权值扩散半径
    double S = 0.95; // 记忆参数
    double U = 2; // 强度参数
    short initial_prob = 50; // 初始可能性
    double k = 0.5; // 扩散系数
    short min = 15; // 最低阈值

    //根据旧边界和路径集合更新boundary
    public Update(Boundary old_boundary, RouteSet route_set, int level) {

        adjustParam(level);

        boundary= old_boundary;
        routes = route_set;
        enclosure = new Boundary();

        double[] realms = get4ExtremeValue(old_boundary, route_set);
        bound_box = new BoundBox(realms[0], realms[1], realms[2], realms[3], radius);
        bound_box.getValues(boundary, radius, S);
        bound_box.trackValues(boundary, routes, radius, U, initial_prob, k);
        bound_box.reap(boundary, min, radius);
        bound_box.showBox();
        bound_box.setEnclosure(enclosure, radius);
    }

    private double[] get4ExtremeValue(Boundary old_boundary, RouteSet route_set) {
        double[] bound_realm = old_boundary.get4ExtremeValue();
        double[] route_realm = route_set.get4ExtremeValue();

        double north = Math.max(bound_realm[0], route_realm[0]);
        double south = Math.min(bound_realm[1], route_realm[1]);
        double east = Math.max(bound_realm[2], route_realm[2]);
        double west = Math.min(bound_realm[3], route_realm[3]);

        double[] result = new double[]{north, south, east, west};
        return result;
    }

    public Boundary getResult() {
        return boundary;
    }

    public Boundary getEnclosure() {return enclosure;}

    private void adjustParam(int level) {
        switch (level){
            case 1: {
                radius = 1;
                break;
            }
            case 2: {
                radius = 2;
                break;
            }
            case 3: {
                radius = 3;
                break;
            }
        }
    }

}
