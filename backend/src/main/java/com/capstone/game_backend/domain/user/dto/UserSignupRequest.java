package com.capstone.game_backend.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignupRequest(
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다.")
        String uid,

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(max = 20, message = "닉네임은 20자를 넘을 수 없습니다")
        String nickname,

        @NotBlank(message = "비밀번호는 필수 입력값입니다")
        @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다")
        String password
) {
}
