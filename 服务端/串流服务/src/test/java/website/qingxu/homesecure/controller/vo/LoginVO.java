package website.qingxu.homesecure.controller.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginVO extends AbstractResult{
    private String token;
}
