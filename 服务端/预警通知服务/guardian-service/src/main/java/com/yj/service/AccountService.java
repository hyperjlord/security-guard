package com.yj.service;

import com.yj.dto.AccountInfo;

public interface AccountService {
    AccountInfo getAccountInfo(String token);

}
