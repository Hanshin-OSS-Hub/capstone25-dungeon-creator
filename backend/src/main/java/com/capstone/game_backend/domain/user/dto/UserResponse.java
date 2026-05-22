package com.capstone.game_backend.domain.user.dto;

import com.capstone.game_backend.domain.user.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record UserResponse(
        Long id,
        String uid,
        String nickname,
        String token
) {

    // 1. 토큰이 필요 없는 경우 (회원가입 응답용)
    public static UserResponse from(UserEntity userEntity) {
        return new UserResponse(userEntity.getId(), userEntity.getUid(), userEntity.getNickname(), null);
    }

    // 2. 토큰을 같이 내려줘야 하는 경우 (로그인 응답용)
    public static UserResponse of(UserEntity userEntity, String token) {
        return new UserResponse(userEntity.getId(), userEntity.getUid(), userEntity.getNickname(), token);
    }
}
