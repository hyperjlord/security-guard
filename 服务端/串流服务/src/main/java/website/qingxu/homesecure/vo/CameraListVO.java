package website.qingxu.homesecure.vo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CameraListVO extends AbstractResult{
    private ArrayList<CameraInfo> list;
}
