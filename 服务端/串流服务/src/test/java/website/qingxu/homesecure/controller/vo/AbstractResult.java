package website.qingxu.homesecure.controller.vo;

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

    public void success(String msg){
        this.setStateCode(StateCode.SUCCESS);
        this.setMsg(msg);
    }

    public void error(String msg){
        this.setStateCode(StateCode.ERROR);
        this.setMsg(msg);
    }

    public void waiting(String msg){
        this.setStateCode(StateCode.WAITING);
        this.setMsg(msg);
    }

    public void warning(String msg){
        this.setStateCode(StateCode.WARNING);
        this.setMsg(msg);
    }

}
