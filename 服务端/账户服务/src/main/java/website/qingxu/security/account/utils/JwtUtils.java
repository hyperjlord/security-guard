package website.qingxu.security.account.utils;

import io.jsonwebtoken.*;
import website.qingxu.security.account.entity.Account;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    private JwtUtils(){}
    public static final String SUBJECT = "home-secure";//签名发行者

    public static final String APPSECRET = "AHIOD)*#183";//签名

    /**
     * 生成token
     *
     * @return token
     */
    public static String genJsonWebToken(Account account) {
        String token = "";
        Map<String, Object> claims= new HashMap<>();
        claims.put("id", account.getId());
        token = Jwts.builder()
                .setSubject(SUBJECT)//发行者
                .setClaims(claims)
                .setIssuedAt(new Date())//发行日期
                .signWith(SignatureAlgorithm.HS256, APPSECRET)//签名
                .compact();
        return token;
    }

    public static long parseIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(APPSECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("id", Long.class);
    }

}
