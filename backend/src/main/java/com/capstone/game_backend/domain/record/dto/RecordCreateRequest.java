package com.capstone.game_backend.domain.record.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class RecordCreateRequest {

    @Min(value = 0, message = "점수는 0보다 작을 수 없습니다.")
    private int score;

    @NotBlank(message = "게임 메타 데이터는 필수입니다.")
    private String gameMeta;

    @Positive(message = "클리어 시간은 1초 이상이어야 합니다.")
    private int clearTimeSeconds;
}
