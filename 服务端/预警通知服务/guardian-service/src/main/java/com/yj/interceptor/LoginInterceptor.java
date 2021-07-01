package com.yj.interceptor;

import com.yj.dto.AccountInfo;
import com.yj.service.AccountService;
import com.yj.service.impl.AccountServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    AccountService accountService=new AccountServiceImpl();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri=request.getRequestURI();
        String token=request.getHeader("token");
        AccountInfo accountInfo;
        if((accountInfo=accountService.getAccountInfo("token"))!=null){
            log.info("token信息：",accountInfo);
            return true;
        }
        return false;
    }
}
