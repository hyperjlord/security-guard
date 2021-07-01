package website.qingxu.security.account.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import website.qingxu.security.account.entity.*;
import website.qingxu.security.account.qo.RegisterQO;
import website.qingxu.security.account.utils.JwtUtils;
import website.qingxu.security.account.utils.RedisUtils;

import java.util.*;
import java.security.SecureRandom;

@Service
public class AccountServiceImpl implements AccountService{
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private GuardRepository guardRepository;
    @Autowired
    private GuardService guardService;

    private static final int TOKEN_EXPIRE_TIME = 30 * 24 * 60 * 60;
    private static final SecureRandom random = new SecureRandom();
    private static final String CHARSET ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";


    @Override
    public AccountInfo getAccountInfo(String token) {
        String accountInfoStr = (String)redisUtils.get(token);
        AccountInfo accountInfo;
        if(accountInfoStr == null){
            accountInfo = renewToken(token);
        }
        else {
            accountInfo = JSON.parseObject(accountInfoStr, AccountInfo.class);
        }
        return accountInfo;
    }

    @Override
    public String login(Account account) {
        String token = JwtUtils.genJsonWebToken(account);
        AccountInfo accountInfo = genAccountInfo(account);
        String accountInfoStr = JSON.toJSONString(accountInfo);
        redisUtils.set(token, accountInfoStr, TOKEN_EXPIRE_TIME);
        account.setToken(token);
        accountRepository.save(account);
        return token;
    }

    @Override
    public String register(RegisterQO request) {
        Account account = new Account();
        account.setPassword(request.getPassword());
        account.setPhone(request.getPhone());
        account.setAccountType(AccountType.FREE);
        account.setNickname(request.getNickname());
        accountRepository.save(account);
        guardService.addGuard(account.getId(), account.getId(), account.getNickname(), account.getNickname());
        String token = JwtUtils.genJsonWebToken(account);
        AccountInfo accountInfo = genAccountInfo(account);
        account.setToken(token);
        accountRepository.save(account);
        String accountInfoStr = JSON.toJSONString(accountInfo);
        redisUtils.set(token, accountInfoStr, TOKEN_EXPIRE_TIME);
        return token;
    }

    @Override
    public AccountInfo genAccountInfo(Account account){
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(account.getId());
        accountInfo.setPhone(account.getPhone());
        accountInfo.setNickname(account.getNickname());
        accountInfo.setAccountType(account.getAccountType());

        ArrayList<GuardInfo> wardInfos = new ArrayList<>();
        List<Guard> wards = guardRepository.findAllByGuardian(account.getId());
        for(Guard ward : wards){
            wardInfos.add(new GuardInfo(ward.getWard(), ward.getWardName()));
        }
        accountInfo.setWards(wardInfos);

        ArrayList<GuardInfo> guardianInfos = new ArrayList<>();
        List<Guard> guardians = guardRepository.findAllByWard(account.getId());
        for(Guard guardian : guardians){
            guardianInfos.add(new GuardInfo(guardian.getGuardian(), guardian.getGuardianName()));
        }
        accountInfo.setGuardians(guardianInfos);
        return accountInfo;

    }

    @Override
    public String genShareCode(String token, int shareCodeExpireTime) {
        AccountInfo accountInfo = getAccountInfo(token);
        if(accountInfo == null){
            return null;
        }
        ShareCodeInfo shareCodeInfo = new ShareCodeInfo(accountInfo.getId(), genRandomString(5));
        if(redisUtils.set(transToShareCodeKey(accountInfo.getPhone()), shareCodeInfo, shareCodeExpireTime)){
            return shareCodeInfo.getShareCode();
        }
        return null;
    }



    @Override
    public ShareCodeInfo getShareCodeInfo(String phone) {
        return (ShareCodeInfo)redisUtils.get(transToShareCodeKey(phone));
    }

    @Override
    public void cleanShareCode(String phone) {
        redisUtils.delete(transToShareCodeKey(phone));
    }

    @Override
    public void cleanAccountInfo(String token) {
        Optional<Account> optionalAccount = accountRepository.findById(getAccountInfo(token).getId());
        if(optionalAccount.isPresent()){
            Account account = optionalAccount.get();
            account.setToken("");
            accountRepository.save(account);
        }
        redisUtils.delete(token);
    }

    @Override
    public AccountInfo renewToken(String token) {
        AccountInfo accountInfo = null;
        Long id = JwtUtils.parseIdFromToken(token);
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if(optionalAccount.isPresent()){
            Account account = optionalAccount.get();
            if(account.getToken().equals(token)){
                accountInfo = genAccountInfo(account);
                redisUtils.set(token, accountInfo, TOKEN_EXPIRE_TIME);
            }
        }
        return accountInfo;
    }

    private String transToShareCodeKey(String phone){
        return "ShareCode:" + phone;
    }

    private String genRandomString(int length){
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }
}
