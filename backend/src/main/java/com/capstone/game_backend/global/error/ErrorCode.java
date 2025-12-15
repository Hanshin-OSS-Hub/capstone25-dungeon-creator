package com.capstone.game_backend.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DUPLICATE_UID("이미 존재하는 UID입니다."),
    DUPLICATE_NICKNAME("이미 존재하는 닉네임입니다.");

    private final String message;
}
