package com.capstone.game_backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String uid;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
