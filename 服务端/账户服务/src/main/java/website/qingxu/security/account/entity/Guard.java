package website.qingxu.security.account.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "guard")
@Getter
@Setter
@IdClass(GuardPK.class)
public class Guard implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "guardian")
    private long guardian;

    @Id
    @Column(name = "ward")
    private long ward;

    @Column(name = "guardian_name")
    private String guardianName;

    @Column(name = "ward_name")
    private String wardName;

}
