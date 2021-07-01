package website.qingxu.camerademo.entity;

public class UserInfo {
    public String selfPhone;
    public String nickName;
    public String guardPhone;
    public String guardName;
    public UserInfo(String selfPhone,String nickName,String guardPhone,String guardName){
        this.selfPhone=selfPhone;
        this.nickName=nickName;
        this.guardName=guardName;
        this.guardPhone=guardPhone;
    }

    public String getGuardName() {
        return guardName;
    };

    public String getNickName() {
        return nickName;
    };

    public String getGuardPhone() {
        return guardPhone;
    };

    public String getSelfPhone() {
        return selfPhone;
    }
}
