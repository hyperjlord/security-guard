package com.yj.service.impl;

import com.alibaba.fastjson.JSON;
import com.yj.dto.AccountInfo;
import com.yj.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;

    private static class AccountInfoVO {
        public int stateCode;
        public String msg;
        public AccountInfo accountInfo;
    }

    private static class RenewTokenQO {
        public String token;
    }

    @Override
    public AccountInfo getAccountInfo(String token) {
        String accountInfoStr = (String) redisTemplate.opsForValue().get(token);
        AccountInfo accountInfo;
        if (accountInfoStr == null) {
            RenewTokenQO request = new RenewTokenQO();
            request.token = token;
            AccountInfoVO accountInfoVO = restTemplate.postForObject(
                    "http://ACCOUNT-PROVIDER/v0.0.1/account/renewToken",
                    request,
                    AccountInfoVO.class
            );
            if (accountInfoVO == null || accountInfoVO.stateCode != 0) {
                accountInfo = null;
            } else {
                accountInfo = accountInfoVO.accountInfo;
            }
        }
        else {
            accountInfo = JSON.parseObject(accountInfoStr, AccountInfo.class);
        }
        return accountInfo;
    }
}


