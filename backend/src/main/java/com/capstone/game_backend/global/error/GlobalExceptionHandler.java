package com.capstone.game_backend.global.error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        // JSON 형태로 응답할 객체 생성
        ErrorResponse response = new ErrorResponse(errorCode.name(), errorCode.getMessage());

        // Enum에 정의된 HTTP Status를 그대로 반환
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    // DB의 Unique 제약 조건 위배 등 동시성 충돌로 발생하는 에러 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        ErrorResponse response = new ErrorResponse(
                "RESOURCE_CONFLICT",
                "이미 존재하는 데이터이거나, 처리 중 충돌이 발생했습니다."
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 상태 코드 반환
    }

    // Validation(@Valid) 검증 실패 시 발생하는 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // DTO에 적어둔 여러 개의 에러 메시지 중 첫 번째 메시지만 뽑기
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ErrorResponse response = new ErrorResponse("VALIDATION_FAILED", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 400 상태 코드 반환
    }
}
