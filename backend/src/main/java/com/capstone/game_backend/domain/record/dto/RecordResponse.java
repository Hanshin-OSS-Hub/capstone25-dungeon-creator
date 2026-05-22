package com.capstone.game_backend.domain.record.dto;

import com.capstone.game_backend.domain.record.RecordEntity;
import com.fasterxml.jackson.annotation.JsonRawValue;

public record RecordResponse(
        Long id,

        @JsonRawValue
        String gameMeta,

        int playTimeSeconds
) {

    public static RecordResponse from(RecordEntity record) {
        return new RecordResponse(
                record.getId(),
                record.getGameMeta(),
                record.getPlayTimeSeconds()
        );
    }
}
