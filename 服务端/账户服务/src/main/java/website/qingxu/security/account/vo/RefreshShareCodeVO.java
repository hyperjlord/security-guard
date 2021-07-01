package website.qingxu.security.account.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshShareCodeVO extends AbstractResult{
    private String shareCode;
}
