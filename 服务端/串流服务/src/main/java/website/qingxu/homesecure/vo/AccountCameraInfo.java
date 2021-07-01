package website.qingxu.homesecure.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCameraInfo {
    private long accountId;
    private String name;
    private ArrayList<CameraInfo> cameraList;
}
