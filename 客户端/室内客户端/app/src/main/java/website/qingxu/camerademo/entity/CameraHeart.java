package website.qingxu.camerademo.entity;

public class CameraHeart {
    public String rmpUrl;
    public int state;//状态，是否开启相机
    public String msg;//心跳信息
    public CameraHeart(String rmpUrl,int state,String msg){
        this.rmpUrl=rmpUrl;
        this.state=state;
        this.msg=msg;
    }

}
