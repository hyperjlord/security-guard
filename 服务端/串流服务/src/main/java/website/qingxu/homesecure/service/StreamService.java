package website.qingxu.homesecure.service;

import website.qingxu.homesecure.entity.Camera;
import website.qingxu.homesecure.vo.AbstractResult;

import java.util.Optional;

public interface StreamService {
    boolean checkCameraExist(Optional<Camera> optionalCamera, AbstractResult res);
}
