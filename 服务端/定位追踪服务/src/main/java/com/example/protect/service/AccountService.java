package com.example.protect.service;


import com.example.protect.entities.AccountInfo;

public interface AccountService {
    AccountInfo getAccountInfo(String token);
}
