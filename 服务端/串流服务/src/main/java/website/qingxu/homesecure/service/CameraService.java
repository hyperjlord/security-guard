package website.qingxu.homesecure.service;

import website.qingxu.homesecure.entity.AccountInfo;
import website.qingxu.homesecure.vo.AccountCameraInfo;
import website.qingxu.homesecure.vo.CameraInfo;

import java.util.ArrayList;

public interface CameraService {
    ArrayList<CameraInfo> getCameraList(long accountId);
    ArrayList<AccountCameraInfo> getAccountCameraList(AccountInfo accountInfo);
}
