package com.capstone.game_backend.domain.record.dto;

import com.capstone.game_backend.domain.record.entity.Record;
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

    public static RecordResponse from(Record record) {
        return new RecordResponse(
                record.getId(),
                record.getGameMeta(),
                record.getPlayTimeSeconds()
        );
    }
}
