package website.qingxu.homesecure.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HeartBeatVO extends AbstractResult{
    private Command command;
}
