package com.capstone.game_backend.domain.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingResponse {

    private String nickname;
    private int rankPosition;
    private double rankScore;
}
