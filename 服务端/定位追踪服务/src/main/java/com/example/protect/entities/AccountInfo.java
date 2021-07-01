package com.example.protect.entities;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AccountInfo {
    public long id;
    public String nickname;
    public int accountType;
    public String phone;
    public ArrayList<GuardInfo> wards;
    public ArrayList<GuardInfo> guardians;
}

