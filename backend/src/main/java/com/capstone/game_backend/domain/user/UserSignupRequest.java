package com.capstone.game_backend.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserSignupRequest {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
    private String uid;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(max = 20, message = "닉네임은 20자를 넘을 수 없습니다")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다")
    private String password;
}
