package com.capstone.game_backend.domain.record.dto;

import lombok.Getter;

@Getter
public class RecordCreateRequest {

    private Long userId;
    private int score;
    private String gameMeta;
}
