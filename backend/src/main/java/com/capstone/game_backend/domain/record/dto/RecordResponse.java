package com.capstone.game_backend.domain.record.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RecordResponse {

    private Long id;
    private int score;
    private String gameMete;
    private LocalDateTime playedAt;
}
