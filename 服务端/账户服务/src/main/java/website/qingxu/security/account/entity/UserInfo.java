package website.qingxu.security.account.entity;

import lombok.Data;

import java.util.ArrayList;

@Data
public class UserInfo {
    private long id = 1L;
    private String nickname = "大聪明";
    private int accountType = 1;
    private String phone = "18019087170";
    private String shareCode = "qwerty";
    private ArrayList<GuardInfo> wards = new ArrayList<>();
}
