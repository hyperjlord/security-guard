package website.qingxu.homesecure.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import website.qingxu.homesecure.entity.Camera;

import java.util.List;

public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findAllByUserId(long userId);
}
