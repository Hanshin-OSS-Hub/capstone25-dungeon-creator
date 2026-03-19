package com.capstone.game_backend.domain.user.service;

import com.capstone.game_backend.domain.user.dto.UserLoginRequest;
import com.capstone.game_backend.domain.user.dto.UserResponse;
import com.capstone.game_backend.domain.user.dto.UserSignupRequest;
import com.capstone.game_backend.domain.user.entity.User;
import com.capstone.game_backend.domain.user.repository.UserRepository;
import com.capstone.game_backend.global.error.CustomException;
import com.capstone.game_backend.global.error.ErrorCode;
import com.capstone.game_backend.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public UserResponse signup(UserSignupRequest req){
        if(userRepository.existsByUid(req.getUid()))
            throw new CustomException(ErrorCode.DUPLICATE_UID);

        if(userRepository.existsByNickname(req.getNickname()))
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);

        User user = User.builder()
                .uid(req.getUid())
                .nickname(req.getNickname())
                // encode()로 평문 암호화해서 DB에 넣기
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .build();

        userRepository.save(user);

        // 기획 의도에 따른 선택:
        // 회원가입 직후 자동 로그인을 시킬 거라면 여기서도 token을 발급해서 준다
        return UserResponse.from(user);
    }

    // 로그인
    public UserResponse login(UserLoginRequest req){
        // 1. 아이디로 유저 찾기 (없으면 바로 로그인 실패 처리)
        User user = userRepository.findByUid(req.getUid())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // 2. 비밀번호 검증: matches(입력받은 평문, DB에 저장된 해시값)
        if(!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS); // 비밀번호가 틀려도 똑같은 에러로
        }

        // 1. 비밀번호까지 맞았다면, JwtUtil로 토큰 생성
                String token = jwtUtil.generateToken(user.getUid());

        // 2. 응답 데이터에 토큰을 담아서 보내기
        return UserResponse.of(user, token);
    }

    // 유저 탈퇴
    @Transactional
    public void deleteUser(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.withdraw();
    }
}
