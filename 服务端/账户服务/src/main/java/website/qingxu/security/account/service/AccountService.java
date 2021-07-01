package website.qingxu.security.account.service;

import website.qingxu.security.account.entity.Account;
import website.qingxu.security.account.entity.AccountInfo;
import website.qingxu.security.account.entity.ShareCodeInfo;
import website.qingxu.security.account.qo.RegisterQO;

public interface AccountService {
    String login(Account account);
    String register(RegisterQO request);
    AccountInfo getAccountInfo(String token);
    AccountInfo genAccountInfo(Account account);
    String genShareCode(String token, int shareCodeExpireTime);
    ShareCodeInfo getShareCodeInfo(String phone);
    void cleanShareCode(String phone);
    void cleanAccountInfo(String token);
    AccountInfo renewToken(String token);

}
