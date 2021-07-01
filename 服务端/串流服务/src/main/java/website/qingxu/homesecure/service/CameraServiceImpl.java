package website.qingxu.homesecure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import website.qingxu.homesecure.dao.CameraRepository;
import website.qingxu.homesecure.entity.AccountInfo;
import website.qingxu.homesecure.entity.Camera;
import website.qingxu.homesecure.entity.GuardInfo;
import website.qingxu.homesecure.vo.AccountCameraInfo;
import website.qingxu.homesecure.vo.CameraInfo;

import java.util.ArrayList;
import java.util.List;

@Service
public class CameraServiceImpl implements CameraService {
    @Autowired
    private CameraRepository cameraRepository;

    @Override
    public ArrayList<CameraInfo> getCameraList(long accountId) {
        List<Camera> cameraList = cameraRepository.findAllByUserId(accountId);
        ArrayList<CameraInfo> cameraInfos = new ArrayList<>();
        for (Camera camera : cameraList) {
            cameraInfos.add(new CameraInfo(
                    camera.getCameraId(),
                    camera.getCameraName()));
        }
        return cameraInfos;
    }

    @Override
    public ArrayList<AccountCameraInfo> getAccountCameraList(AccountInfo accountInfo) {
        ArrayList<AccountCameraInfo> accountCameraInfos = new ArrayList<>();
        for (GuardInfo ward : accountInfo.getWards()) {
            accountCameraInfos.add(new AccountCameraInfo(
                    ward.getAccountId(),
                    ward.getName(),
                    this.getCameraList(ward.getAccountId())
            ));
        }
        return accountCameraInfos;
    }
}
