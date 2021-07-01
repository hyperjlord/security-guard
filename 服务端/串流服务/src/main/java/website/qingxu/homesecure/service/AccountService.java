package website.qingxu.homesecure.service;

import website.qingxu.homesecure.entity.AccountInfo;

public interface AccountService {
    AccountInfo getAccountInfo(String token);
}
