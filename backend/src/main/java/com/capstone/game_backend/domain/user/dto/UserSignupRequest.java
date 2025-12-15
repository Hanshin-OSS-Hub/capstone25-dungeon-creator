package com.capstone.game_backend.domain.user.dto;

import lombok.Getter;

@Getter
public class UserSignupRequest {

    private String uid;
    private String nickname;
    private String password;
}
