package website.qingxu.homesecure.service;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import website.qingxu.homesecure.entity.AccountInfo;



@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RestTemplate restTemplate;

    @Data
    private static class AccountInfoVO {
        private int stateCode;
        private String msg;
        private AccountInfo accountInfo;
    }

    @Data
    private static class RenewTokenQO {
        private String token;
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
            if (accountInfoVO == null || accountInfoVO.getStateCode() != 0) {
                accountInfo = null;
            } else {
                accountInfo = accountInfoVO.getAccountInfo();
            }
        }
        else {
            accountInfo = JSON.parseObject(accountInfoStr, AccountInfo.class);
        }
        return accountInfo;
    }
}


