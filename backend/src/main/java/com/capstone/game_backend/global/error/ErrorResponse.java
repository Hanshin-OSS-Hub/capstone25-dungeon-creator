package com.capstone.game_backend.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String message;   // 유저에게 보여줄 팝업 내용
}