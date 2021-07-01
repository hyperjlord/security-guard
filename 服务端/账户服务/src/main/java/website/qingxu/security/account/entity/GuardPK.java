package website.qingxu.security.account.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GuardPK implements Serializable {
    private static final long serialVersionUID = 1L;
    private long guardian;
    private long ward;
}
