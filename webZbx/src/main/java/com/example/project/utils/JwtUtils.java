package com.example.project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; // 新增导入

import javax.crypto.SecretKey; // 新增导入
import java.util.Date;

public class JwtUtils {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 修改为SecretKey类型
    private static final long EXPIRATION_TIME = 86400000; // 24小时

    public static String generateToken(String username) {
        Date expiration = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiration)
                .signWith(SECRET_KEY) // 移除SignatureAlgorithm参数
                .compact();
    }

    public static String validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder() // 使用parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
