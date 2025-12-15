package com.capstone.game_backend.domain.user.service;

import com.capstone.game_backend.domain.user.dto.UserLoginRequest;
import com.capstone.game_backend.domain.user.dto.UserResponse;
import com.capstone.game_backend.domain.user.dto.UserSignupRequest;
import com.capstone.game_backend.domain.user.entity.User;
import com.capstone.game_backend.domain.user.repository.UserRepository;
import com.capstone.game_backend.global.error.CustomException;
import com.capstone.game_backend.global.error.ErrorCode;
import com.capstone.game_backend.global.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원가입
    public UserResponse signup(UserSignupRequest req){
        if(userRepository.existsByUid(req.getUid()))
            throw new CustomException(ErrorCode.DUPLICATE_UID);

        if(userRepository.existsByNickname(req.getNickname()))
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);

        User user = User.builder()
                .uid(req.getUid())
                .nickname(req.getNickname())
                .passwordHash(PasswordUtil.hash(req.getPassword()))
                .build();

        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUid(), user.getNickname());
    }

    // 로그인
    public UserResponse login(UserLoginRequest req){
        User user = userRepository.findByUid(req.getUid())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!user.getPasswordHash().equals(PasswordUtil.hash(req.getPassword())))
            throw new CustomException(ErrorCode.USER_NOT_FOUND);

        return new UserResponse(user.getId(), user.getUid(), user.getNickname());
    }
}
