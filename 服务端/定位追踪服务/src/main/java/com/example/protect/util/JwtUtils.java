package com.example.protect.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {
    public static final String SUBJECT = "home-secure";//签名发行者

    public static final String APPSECRET = "AHIOD)*#183";//签名

    /**
     * 生成token
     *
     * @return token
     */
    public static String genJsonWebToken() {
        String token = "";
        token = Jwts.builder()
                .setSubject(SUBJECT)//发行者
                .claim("userId", "ssss")
                .setIssuedAt(new Date())//发行日期
                .signWith(SignatureAlgorithm.HS256, APPSECRET).compact();//签名

        return token;
    }
}
