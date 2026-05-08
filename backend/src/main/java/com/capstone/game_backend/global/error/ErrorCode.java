package com.capstone.game_backend.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // 409 Conflict (데이터 충돌/중복)
    DUPLICATE_UID(HttpStatus.CONFLICT, "이미 존재하는 UID입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),

    // 401 Unauthorized (인증 실패)
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 잘못되었습니다."),

    // 400 Bad Request (잘못된 요청)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다. (JSON 형식을 확인해주세요)");

    private final HttpStatus httpStatus;
    private final String message;
}