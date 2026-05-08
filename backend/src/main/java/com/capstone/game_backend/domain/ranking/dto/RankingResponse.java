package com.capstone.game_backend.domain.ranking.dto;

import com.capstone.game_backend.domain.ranking.entity.Ranking;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RankingResponse {

    private String nickname;
    private int rank;
    private int bestPlayTime;  //시간

    public static RankingResponse of(Ranking ranking, int calculatedRank) {
        return new RankingResponse(
                ranking.getUser().getNickname(),
                calculatedRank,
                ranking.getBestPlayTime()
        );
    }

    // 랭킹 없는 경우
    public static RankingResponse unranked(String nickname) {
        // 랭킹, 점수 0으로 보내기
        return new RankingResponse(nickname, 0, 0);
    }
}
