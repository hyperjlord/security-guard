package website.qingxu.camerademo.entity;

/**
 * 从LoginRepository检索到的已登录用户，捕获用户的信息类
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoginUser {

    private String userName;//用户名
    private String password;//密码
    public LoginUser(String userName, String password){
        this.userName=userName;
        this.password=password;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}
