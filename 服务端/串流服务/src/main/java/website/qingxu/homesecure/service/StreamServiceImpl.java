package website.qingxu.homesecure.service;

import org.springframework.stereotype.Service;
import website.qingxu.homesecure.entity.Camera;
import website.qingxu.homesecure.vo.AbstractResult;

import java.util.Optional;

@Service
public class StreamServiceImpl implements StreamService{
    @Override
    public boolean checkCameraExist(Optional<Camera> optionalCamera, AbstractResult res) {
        if (!optionalCamera.isPresent()) {
            res.error("未找到目标摄像头，请刷新后重试");
            return false;
        }
        return true;
    }
}
