package com.property.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成 JWT Token
     * @param id 用户ID
     * @param username 用户名
     * @param role 角色 (0=管理员, 1=业主)
     * @return Token 字符串
     */
    public String generateToken(Integer id, String username, Integer role) {
        return JWT.create()
                .withClaim("id", id)
                .withClaim("username", username)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 解析 Token
     */
    public DecodedJWT parseToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        return verifier.verify(token);
    }

    /**
     * 校验 Token 是否有效
     */
    public boolean validate(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 获取用户ID
     */
    public Integer getUserId(String token) {
        return parseToken(token).getClaim("id").asInt();
    }

    /**
     * 从 Token 获取用户名
     */
    public String getUsername(String token) {
        return parseToken(token).getClaim("username").asString();
    }

    /**
     * 从 Token 获取角色
     */
    public Integer getRole(String token) {
        return parseToken(token).getClaim("role").asInt();
    }
}
