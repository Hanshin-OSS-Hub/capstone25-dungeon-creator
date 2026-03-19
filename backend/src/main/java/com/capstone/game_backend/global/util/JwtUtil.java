package com.capstone.game_backend.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationTime;

    // yml 파일에서 설정한 값들을 불러와서 초기화
    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long expirationTime
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationTime = expirationTime;
    }

    // 1. JWT 토큰 생성 (로그인 성공 시 호출됨)
    public String generateToken(String uid) {
        return Jwts.builder()
                .setSubject(uid)
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. JWT 토큰 검증 및 UID 추출 (API 요청이 올 때마다 호출됨)
    public String getUidFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // 진짜 토큰인지 확인
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // uid만 빼서 반환
    }
}