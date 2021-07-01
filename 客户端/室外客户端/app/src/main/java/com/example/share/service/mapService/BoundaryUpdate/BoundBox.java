package com.example.share.service.mapService.BoundaryUpdate;

public class BoundBox {

    double[][] bound_box; //二维包围盒主体
    int width; //包围盒宽
    int height; //包围盒高
    double north_lat; //
    double south_lat; //
    double east_lng; //
    double west_lng; //

    //构造函数，构造二维边界包围盒
    public BoundBox(double north, double south, double east, double west, int radius) {
        /**
         * north: 最北纬
         * south: 最南纬
         * east: 最东经
         * west: 最西经
         * radius: 权值扩散半径*/

        north_lat = north;
        south_lat = south;
        east_lng = east;
        west_lng = west;

        //确定包围盒宽高
        width = ((int)((Operation.sub(east,west))*Math.pow(10,6))+1) + 2*radius;
        height = ((int)((Operation.sub(north,south))*Math.pow(10,6))+1) + 2*radius;
        bound_box = new double[height][width];
    }

    //将经纬度转化为包围盒的坐标
    public int[] transform(double lat, double lng, int radius) {
        int x = (int)(Operation.sub(lat, south_lat)*Math.pow(10,6)) + radius;
        int y = (int)(Operation.sub(lng, west_lng)*Math.pow(10,6)) + radius;
        int[] location = new int[]{x,y};
        return location;
    }

    //将包围盒的坐标转化为经纬度
    public double[] transform_reverse(int x, int y, int radius) {
        double lat = Operation.sum((x-radius)*Math.pow(10,-6), south_lat);
        double lng = Operation.sum(((y-radius)*Math.pow(10,-6)), west_lng);
        double[] location = new double[]{lat,lng};
        return location;
    }

    //将boundary中的点进行衰减处理并存进box
    public void getValues(Boundary boundary, int radius, double S) {
        for (Point point : boundary.boundary) {
            int[] location = transform(point.latitude, point.longitude, radius);
            point.untraveled_weeks += 1;
            double ratio = Operation.decrease(point, S);
            point.value *= ratio;
            bound_box[location[0]][location[1]] = point.value;
        }
    }

    //根据本周路径修改包围盒中各坐标值参数
    public void trackValues(Boundary boundary, RouteSet route_set, int radius, double U, double initial_prob, double k) {
        /**
         * boundary: 边界
         * route_set: 路径集合
         * radius: 权值扩散半径
         * U: 强度参数
         * initial_prob: 初始可能性
         * k: 扩散系数*/
        for (Route route : route_set.route_set) {
            for (Point point : route.route) {
                int[] location = transform(point.latitude, point.longitude, radius);
                if (boundary.isInBound(point)) {
                    bound_box[location[0]][location[1]] *= U;
                    boundary.frequent2zero(point);
                }
                else {
                    point.untraveled_weeks = 0;
                    boundary.addPoint(point);
                    bound_box[location[0]][location[1]] = initial_prob;
                }
                for (int i = -1*radius; i <= radius; i++) {
                    for (int j = -1*radius; j <= radius; j++) {
                        double distance = Math.sqrt(i*i+j*j);
                        if (distance > 0 && distance < Math.sqrt(2)*radius) {
                            double up = Math.pow(Math.E,(-1*k*distance));
                            if(bound_box[location[0]+i][location[1]+j] == 0) {
                                bound_box[location[0]+i][location[1]+j] += up * initial_prob;
                            }
                            else {
                                bound_box[location[0]+i][location[1]+j] *= 1+(up * (U-1));
                            }
                        }
                    }
                }
            }
        }
    }


    //收割最后的边界
    public void reap(Boundary boundary, double min, int radius) {
        int i;
        int j;
        double[] location;
        /**
         * boundary: 边界
         * min: 最低阈值
         * radius: 权值扩散半径*/
        for (i = 0; i < height; i++) {
            for (j = 0; j < width; j++) {
                location = transform_reverse(i, j, radius);
                if (bound_box[i][j] < min) {
                    bound_box[i][j] = 0;
                    if(boundary.isInBound(location[0],location[1])) {
                        boundary.deletePoint(location[0],location[1]);
                    }
                }
                else {
                    bound_box[i][j] = bound_box[i][j]>1?1:bound_box[i][j];
                    if(boundary.isInBound(location[0],location[1])) {
                        //boundary.search(location[0],location[1]).untraveled_weeks = 0;
                        boundary.search(location[0],location[1]).value = bound_box[i][j];
                    }
                    else {
                        Point new_point = new Point(location[0], location[1], bound_box[i][j], 0);
                        boundary.addPoint(new_point);
                    }
                }
            }
        }
    }

    private boolean isInBox(int i, int j) {
        if (i >= 0 && i < height && j >= 0 && j < width) return true;
        else return false;
    }

    //设置边界围栏
    public void setEnclosure(Boundary enclosure, int radius) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double[] location = transform_reverse(i, j, radius);
                if(bound_box[i][j] > 0) {
                    boolean flag = false;
                    int[][] bias = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
                    for (int k=0; k<8;k++){
                        int ni = i+bias[k][0];
                        int nj = j+bias[k][1];
                        if(!isInBox(ni,nj) || isInBox(ni,nj) && bound_box[ni][nj]==0) {
                            flag = true;
                            break;
                        }
                    }
                    if(flag) {
                        Point closure = new Point(location[0], location[1], bound_box[i][j], 0);
                        enclosure.addPoint(closure);
                    }
                }
            }
        }
    }

    public void showBox() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if(bound_box[i][j] > 0){
                    System.out.print("#");
                }
                else{
                    System.out.print(" ");
                }
            }
            System.out.print('\n');
        }
    }

}
