package website.qingxu.security.account.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import website.qingxu.security.account.entity.AccountInfo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoVO extends AbstractResult{
    private AccountInfo accountInfo;
}
