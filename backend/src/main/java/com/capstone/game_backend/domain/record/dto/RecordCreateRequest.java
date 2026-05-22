package com.capstone.game_backend.domain.record.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RecordCreateRequest (
        @NotBlank(message = "게임 메타 데이터는 필수입니다.")
        String gameMeta,

        @Min(value = 0, message = "플레이 시간은 0초 이상이어야 합니다.")
        int playTimeSeconds
) {
}
