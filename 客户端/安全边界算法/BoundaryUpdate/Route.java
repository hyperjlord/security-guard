package com.example.share.service.mapService.BoundaryUpdate;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Route {

    public Route() {
        route = new LinkedList<>();
    }
    public Queue<Point> route; //坐标点队列

    //得到这条路径中的四个极端坐标
    public double[] get4ExtremeValue() {
        double north = -91, south = 91, east = -181, west = 181;
        for (Point point : route) {
            if (point.latitude > north) north = point.latitude;
            if (point.latitude < south) south = point.latitude;
            if (point.longitude > east) east = point.longitude;
            if (point.longitude < west) west = point.longitude;
        }
        double[] result = new double[]{north, south, east, west};
        return result;
    }

    public void addPoint(Point point) {
        route.add(point);
    }

    public void patchSelf() {
        if(route.size() < 2) return;
        Point old_point = route.remove();
        Point new_point = new Point();
        Queue<Point> link_route = new LinkedList<>();
        link_route.add(old_point);
        while(!route.isEmpty()) {
            new_point = route.remove();
            patchLine(old_point, new_point, link_route);
            old_point = new_point;
        }
        route = link_route;
    }

    private void patchLine(Point old_point, Point new_point, Queue<Point> link_route){
        Queue<Point> temp_route = new LinkedList<>();
        Point from = new Point();
        Point to = new Point();
        double x, y, dx, dy, k;
        dx = Operation.sub(new_point.longitude, old_point.longitude);
        dy = Operation.sub(new_point.latitude, old_point.latitude);
        if (dx > 0) {
            from = old_point;
            to = new_point;
        } else {
            from = new_point;
            to = old_point;
        }
        if (dx != 0) {
            k = dy / dx;
            if (Math.abs(k) <= 1) {
                y = from.latitude;
                for (x = from.longitude; x <= to.longitude; x = Operation.sum(x, 0.00001)) {
                    //TODO: 连接点的prob怎么赋予啊？
                    Point point = new Point( (double) Math.round(y * 100000) / 100000, x, (short) 0, 0);
                    temp_route.add(point);
                    y = Operation.sum(y, k * 0.00001);
                }
            }
            else {
                k = 1 / k;
                if (dy > 0) {
                    from = old_point;
                    to = new_point;
                } else {
                    from = new_point;
                    to = old_point;
                }
                x = from.longitude;
                for (y = from.latitude; y <= to.latitude; y = Operation.sum(y, 0.00001)) {
                    Point point = new Point(y, (double) Math.round(x * 100000) / 100000, (short) 0, 0);
                    temp_route.add(point);
                    x = Operation.sum(x, k * 0.00001);
                }
            }
        } else {
            if (dy > 0) {
                from = old_point;
                to = new_point;
            } else {
                from = new_point;
                to = old_point;
            }
            x = from.longitude;
            for (y = from.latitude; y <= to.latitude; y = Operation.sum(y, 0.00001)) {
                Point point = new Point(y, x, (short) 0, 0);
                temp_route.add(point);
            }
        }
        //TODO: 因不清楚prob的值如何赋予，暂时只能这么写
        if (temp_route.element().latitude == old_point.latitude && temp_route.element().longitude == old_point.longitude) {
            temp_route.remove();
            link_route.addAll(temp_route);
        } else {
            Stack<Point> stack = new Stack<>();
            while(!temp_route.isEmpty()) {
                stack.push(temp_route.remove());
            }
            stack.pop();
            while(!stack.empty()){
                link_route.add(stack.pop());
            }
        }
    }

}
