package website.qingxu.homesecure.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "camera")
@Getter
@Setter
public class Camera implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "camera_id")
    @Column(name = "camera_id")
    private long cameraId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "camera_name")
    private String cameraName;

    @Column(name = "stream_pass")
    private String streamPass;

    @Column(name = "stream_room")
    private String streamRoom;

    @Column(name = "camera_sn")
    private String cameraSn;


}
