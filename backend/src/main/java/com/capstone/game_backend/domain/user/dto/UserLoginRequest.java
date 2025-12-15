package com.capstone.game_backend.domain.user.dto;

import lombok.Getter;

@Getter
public class UserLoginRequest {

    private String uid;
    private String password;
}
