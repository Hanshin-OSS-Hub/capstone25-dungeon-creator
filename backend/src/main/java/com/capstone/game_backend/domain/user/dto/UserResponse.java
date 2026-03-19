package com.capstone.game_backend.domain.user.dto;

import com.capstone.game_backend.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    private Long id;
    private String uid;
    private String nickname;
    private String token;

    // 1. 토큰이 필요 없는 경우 (회원가입 응답용)
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUid(), user.getNickname(), null);
    }

    // 2. 토큰을 같이 내려줘야 하는 경우 (로그인 응답용)
    public static UserResponse of(User user, String token) {
        return new UserResponse(user.getId(), user.getUid(), user.getNickname(), token);
    }
}
