package com.capstone.game_backend.domain.ranking.service;

import com.capstone.game_backend.domain.ranking.dto.RankingResponse;
import com.capstone.game_backend.domain.ranking.entity.Ranking;
import com.capstone.game_backend.domain.ranking.repository.RankingRepository;
import com.capstone.game_backend.domain.user.entity.User;
import com.capstone.game_backend.domain.user.repository.UserRepository;
import com.capstone.game_backend.global.error.CustomException;
import com.capstone.game_backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final RankingRepository rankingRepository;
    private final UserRepository userRepository;

    // 랭킹 TOP 100 조회
    public List<RankingResponse> getTop100Ranking(){

        List<Ranking> rankings = rankingRepository.findTop100ByOrderByRankScoreDescUpdatedAtAsc();

        List<RankingResponse> responseList = new ArrayList<>();
        for (int i = 0; i < rankings.size(); i++) {
            responseList.add(RankingResponse.of(rankings.get(i), i + 1));
        }

        return responseList;
    }

    // 유저 랭킹 조회
    public RankingResponse getRankingByNickname(String nickname){

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 랭킹 기록이 없는경우의 랭킹 조회 경우 에러말고 순위 외 객체 리턴하기
        return rankingRepository.findByUserId(user.getId())
                .map(ranking -> {
                    long higherCount = rankingRepository.calculateMyRank(
                            ranking.getRankScore(),
                            ranking.getUpdatedAt()
                    );
                    return RankingResponse.of(ranking, (int) higherCount + 1);
                })
                .orElseGet(() -> RankingResponse.unranked(nickname));
    }

    // 내 랭킹 조회
    public RankingResponse getMyRanking(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return rankingRepository.findByUserId(user.getId())
                .map(ranking -> {
                    long higherCount = rankingRepository.calculateMyRank(
                            ranking.getRankScore(),
                            ranking.getUpdatedAt()
                    );
                    return RankingResponse.of(ranking, (int) higherCount + 1);
                })
                .orElseGet(() -> RankingResponse.unranked(user.getNickname()));
    }

    // 전적이 저장될 때마다 호출될 "랭킹 갱신" 로직
    @Transactional
    public void updateScoreIfBest(User user, int newScore) {

        rankingRepository.findByUserId(user.getId()).ifPresentOrElse(
                // 1. 이미 랭킹 기록이 있는 유저 -> 기존 점수보다 높을 때만 갱신
                ranking -> {
                    if (newScore > ranking.getRankScore()) {
                        // 기존 점수보다 높을 때만 점수를 갱신
                        ranking.updateScore(newScore);
                    }
                },
                // 2. 랭킹 기록이 아예 없는 유저 (첫 클리어) -> 새로 만들기
                () -> {
                    Ranking newRanking = Ranking.builder()
                            .user(user)
                            .rankScore(newScore)
                            .build();
                    rankingRepository.save(newRanking);
                }
        );
    }
}
