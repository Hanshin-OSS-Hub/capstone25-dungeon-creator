package com.capstone.game_backend.domain.ranking.dto;

import com.capstone.game_backend.domain.ranking.RankingEntity;

public record RankingResponse (
        String nickname,
        int rank,
        int bestPlayTime  //시간
) {

    public static RankingResponse of(RankingEntity rankingEntity, int calculatedRank) {
        return new RankingResponse(
                rankingEntity.getUserEntity().getNickname(),
                calculatedRank,
                rankingEntity.getBestPlayTime()
        );
    }

    // 랭킹 없는 경우
    public static RankingResponse unranked(String nickname) {
        // 랭킹, 점수 0으로 보내기
        return new RankingResponse(nickname, 0, 0);
    }
}
