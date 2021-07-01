package website.qingxu.security.account.qo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyShareCodeQO {
    private String phone;
    private String shareCode;
    private String guardianName;
    private String wardName;
}
