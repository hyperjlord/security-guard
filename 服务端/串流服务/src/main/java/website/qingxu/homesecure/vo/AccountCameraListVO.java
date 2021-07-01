package website.qingxu.homesecure.vo;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AccountCameraListVO extends AbstractResult{
    private ArrayList<AccountCameraInfo> accountCameraList;
}
