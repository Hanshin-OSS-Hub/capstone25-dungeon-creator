package com.capstone.game_backend.global.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 유니티가 보낸 HTTP 헤더에서 "Authorization" 값을 꺼낸다
        String authorizationHeader = request.getHeader("Authorization");

        // 2. 토큰이 없거나, "Bearer "로 시작하지 않으면 그냥 통과
        // (로그인 안 한 유저로 취급됨 -> 나중에 SecurityConfig에서 막힘)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 떼어내고 진짜 토큰 문자열만 추출
        String token = authorizationHeader.substring(7);

        try {
            // 4. JwtUtil을 시켜서 토큰에서 유저 ID(uid) 빼오기 (여기서 위조/만료 검증이 자동으로 일어남)
            String uid = jwtUtil.getUidFromToken(token);

            // 5. 검증에 성공했다면, 인증정보를 스프링 시큐리티 컨텍스트에 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(uid, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // 토큰이 만료되었거나 조작되었다면 에러 로그를 남기거나 처리
            logger.error("유효하지 않은 JWT 토큰입니다.");
        }

        // 6. 다음 필터나 컨트롤러로 요청을 넘김
        filterChain.doFilter(request, response);
    }
}