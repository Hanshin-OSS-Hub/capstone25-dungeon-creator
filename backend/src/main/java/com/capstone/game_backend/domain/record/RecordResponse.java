package com.capstone.game_backend.domain.record;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecordResponse {

    private Long id;

    @JsonRawValue
    private String gameMeta; // json객체로 보내기
    private int playTimeSeconds;

    public static RecordResponse from(RecordEntity recordEntity) {
        return new RecordResponse(
                recordEntity.getId(),
                recordEntity.getGameMeta(),
                recordEntity.getPlayTimeSeconds()
        );
    }
}
