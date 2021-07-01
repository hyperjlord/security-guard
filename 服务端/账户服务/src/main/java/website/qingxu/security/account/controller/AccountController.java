package website.qingxu.security.account.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import website.qingxu.security.account.entity.*;
import website.qingxu.security.account.service.*;
import website.qingxu.security.account.vo.*;
import website.qingxu.security.account.qo.*;

import java.util.Optional;


@RestController
@RequestMapping("/v0.0.1/account")
@Api(value = "账户相关接口")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private GuardService guardService;

    private static final int SHARE_CODE_EXPIRE_TIME = 10 * 60;
    private static final String TOKEN_ISSUE_HINT = "登录已过期，请重新登录";

    //用户登录
    @PostMapping(value = "/login")
    @ResponseBody
    public LoginVO login(
            @RequestBody LoginQO request
    ) {
        LoginVO res = new LoginVO();
        Account account;
        Optional<Account> optionalAccount = accountRepository.findByPhone(request.getPhone());
        if(optionalAccount.isEmpty()){
            return (LoginVO) res.error("用户名或密码错误");
        }
        account = optionalAccount.get();
        if(!account.getPassword().equals(request.getPassword())){
            return (LoginVO) res.error("用户名或密码错误");
        }
        res.setToken(accountService.login(account));
        return (LoginVO) res.success("登录成功");
    }

    @PostMapping(value = "/register")
    @ResponseBody
    public LoginVO register(
            @RequestBody RegisterQO request
    ) {
        LoginVO res = new LoginVO();
        if(request.getPhone().length() > 20){
            return (LoginVO) res.error("手机号长度不得超过20位");
        }
        Optional<Account> optionalAccount = accountRepository.findByPhone(request.getPhone());
        if(optionalAccount.isPresent()){
            res.error("手机号已注册，请登录");
            return res;
        }
        if(request.getNickname().length() > 100){
            return (LoginVO) res.error("昵称长度不得超过100");
        }
        if(request.getPassword().length() > 255){
            return (LoginVO) res.error("密码长度不得超过40");
        }
        res.setToken(accountService.register(request));
        return (LoginVO) res.success("登录成功");
    }

    @GetMapping(value = "/refreshShareCode")
    @ResponseBody
    public RefreshShareCodeVO refreshShareCode(
            @RequestHeader("token") String token
    ) {
        RefreshShareCodeVO res = new RefreshShareCodeVO();
        res.setShareCode(accountService.genShareCode(token, SHARE_CODE_EXPIRE_TIME));
        if(res.getShareCode() == null){
            return (RefreshShareCodeVO) res.error("内部问题请重新登录后再试");
        }
        res.success("已设置分享码");
        return res;
    }

    @PostMapping(value = "/applyShareCode")
    @ResponseBody
    public Result applyShareCode(
            @RequestBody ApplyShareCodeQO request,
            @RequestHeader("token") String token
    ) {
        Result res = new Result();
        if(request.getWardName().length() > 100 || request.getGuardianName().length() > 100){
            return (Result) res.error("关系名应小于等于100个字符");
        }
        ShareCodeInfo shareCodeInfo = accountService.getShareCodeInfo(request.getPhone());
        if(shareCodeInfo == null || !shareCodeInfo.getShareCode().equals(request.getShareCode())){
            return (Result) res.error("分享码与手机号不匹配");
        }
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        if(accountInfo == null){
            return (Result) res.error(TOKEN_ISSUE_HINT);
        }
        guardService.addGuard(accountInfo.getId(), shareCodeInfo.getAccountId(), request.getGuardianName(), request.getWardName());
        accountService.cleanShareCode(request.getPhone());
        return (Result) res.error("已绑定监护关系");
    }

    @PutMapping(value = "/changeLoginInfo")
    @ResponseBody
    public Result changeLoginInfo(
            @RequestBody ChangeLoginInfoQO request,
            @RequestHeader("token") String token
    ){
        Result res = new Result();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        if(accountInfo == null){
            return (Result) res.error(TOKEN_ISSUE_HINT);
        }
        Optional<Account> optionalAccount = accountRepository.findById(accountInfo.getId());
        if(optionalAccount.isEmpty()){
            return (Result) res.error("请先注册或重新登录");
        }
        Account account = optionalAccount.get();
        if(request.getPhone() != null){
            if(request.getPhone().length() > 20){
                return (Result) res.error("手机号长度不得超过20位");
            }
            Optional<Account> otherOptionalAccount = accountRepository.findByPhone(request.getPhone());
            if(otherOptionalAccount.isPresent()){
                res.error("手机号绑定到其他账户，请直接登录");
                return res;
            }
            account.setPhone(request.getPhone());
        }
        if(request.getPassword() != null){
            if(request.getPassword().length() > 255){
                return (Result) res.error("密码长度不得超过40");
            }
            account.setPassword(request.getPassword());
        }
        accountRepository.save(account);
        accountService.cleanAccountInfo(token);
        return (Result) res.success("已修改登录信息，请重新登录");
    }

    @PutMapping(value = "/changeNickname")
    @ResponseBody
    public Result changeNickname(
            @RequestBody ChangeNicknameQO request,
            @RequestHeader("token") String token
    ){
        Result res = new Result();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        if(accountInfo == null){
            return (Result) res.error(TOKEN_ISSUE_HINT);
        }
        Optional<Account> optionalAccount = accountRepository.findById(accountInfo.getId());
        if(optionalAccount.isEmpty()){
            return (Result) res.error("请先注册或重新登录");
        }
        Account account = optionalAccount.get();
        if(request.getNickname() != null){
            if(request.getNickname().length() > 100){
                return (Result) res.error("昵称长度不得超过100");
            }
            account.setNickname(request.getNickname());
        }
        accountRepository.save(account);
        accountService.renewToken(token);
        return (Result) res.success("已修改昵称");
    }

    @PutMapping(value = "/removeGuard")
    @ResponseBody
    public Result removeGuard(
            @RequestBody RemoveGuardQO request,
            @RequestHeader("token") String token
    ){
        Result res = new Result();
        AccountInfo accountInfo = accountService.getAccountInfo(token);
        if(accountInfo == null){
            return (Result) res.error(TOKEN_ISSUE_HINT);
        }
        guardService.removeGuard(accountInfo.getId(), request.getWard());
        accountService.renewToken(token);
        return (Result) res.success("已移除监护关系");
    }

    @PostMapping(value = "/renewToken")
    @ResponseBody
    public AccountInfoVO renewToken(
            @RequestBody RenewTokenQO request
    ){
        AccountInfoVO res = new AccountInfoVO();
        res.setAccountInfo(accountService.renewToken(request.getToken()));
        if(res.getAccountInfo() == null){
            return (AccountInfoVO) res.error("无效token");
        }
        return (AccountInfoVO) res.success("已刷新token生命周期");
    }

}
