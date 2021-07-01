package com.yj.entity.pk;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@EqualsAndHashCode
public class GuardPK implements Serializable {
    Long guardian;
    Long ward;
}
