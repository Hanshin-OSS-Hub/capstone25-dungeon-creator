package com.capstone.game_backend.global.config;

import com.capstone.game_backend.global.util.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // 1. 비밀번호 암호화 객체를 Bean으로 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 기본 보안 설정 해제 (API 통신을 위해 필수)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // API 서버는 CSRF 방어를 끕니다
                .formLogin(AbstractHttpConfigurer::disable) // 기본 제공되는 폼 로그인 창 끄기
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 끄기

        // 세션x jwt 사용
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 토큰이 없거나 이상할 때 403 대신 401 에러 만들기
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드
                            response.setContentType("application/json; charset=UTF-8");
                            response.getWriter().write("{\"errorCode\": \"UNAUTHORIZED\", \"message\": \"토큰이 유효하지 않습니다.\"}");
                        })
                )

                // API 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 회원가입, 로그인 누구나 접근 허용
                        .requestMatchers("/user/signup", "/user/login").permitAll()
                        // 랭킹 전체 조회 누구나 볼 수 있게 허용
                        .requestMatchers("/ranking/top100", "/ranking/search").permitAll()
                        // 전적 검색 API 누구나 볼 수 있게 허용 (GET /record)
                        .requestMatchers(HttpMethod.GET, "/record").permitAll()
                        // Swagger UI 및 API 문서 경로 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 그 외(특히 POST /record 처럼 데이터를 저장/수정하는 것)는 무조건 토큰 필요
                        .anyRequest().authenticated()
                )

                // 스프링의 기본 인증 필터가 돌기 전에, JwtFilter 먼저
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}