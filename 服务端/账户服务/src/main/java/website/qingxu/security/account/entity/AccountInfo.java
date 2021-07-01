package website.qingxu.security.account.entity;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AccountInfo {
    private long id;
    private String nickname;
    private int accountType;
    private String phone;
    private ArrayList<GuardInfo> wards;
    private ArrayList<GuardInfo> guardians;
}
