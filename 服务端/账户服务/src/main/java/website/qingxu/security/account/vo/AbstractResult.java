package website.qingxu.security.account.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractResult {
    private int stateCode;
    private String msg;

    public AbstractResult success(String msg){
        this.setStateCode(StateCode.SUCCESS);
        this.setMsg(msg);
        return this;
    }

    public AbstractResult error(String msg){
        this.setStateCode(StateCode.ERROR);
        this.setMsg(msg);
        return this;
    }

    public AbstractResult waiting(String msg){
        this.setStateCode(StateCode.WAITING);
        this.setMsg(msg);
        return this;
    }

    public AbstractResult warning(String msg){
        this.setStateCode(StateCode.WARNING);
        this.setMsg(msg);
        return this;
    }

}
