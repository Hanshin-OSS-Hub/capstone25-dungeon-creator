package com.capstone.game_backend.domain.ranking;

import com.capstone.game_backend.domain.ranking.dto.RankingResponse;
import com.capstone.game_backend.domain.user.UserEntity;
import com.capstone.game_backend.domain.user.UserRepository;
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

        List<RankingEntity> rankingEntities = rankingRepository.findTop100ByOrderByBestPlayTimeAscUpdatedAtAsc();

        List<RankingResponse> responseList = new ArrayList<>();
        for (int i = 0; i < rankingEntities.size(); i++) {
            responseList.add(RankingResponse.of(rankingEntities.get(i), i + 1));
        }

        return responseList;
    }

    // 유저 랭킹 조회
    public RankingResponse getRankingByNickname(String nickname){

        UserEntity userEntity = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 랭킹 기록이 없는경우 에러말고 순위 외 객체 리턴하기
        return rankingRepository.findByUserId(userEntity.getId())
                .map(rankingEntity -> {
                    long higherCount = rankingRepository.calculateMyRank(
                            rankingEntity.getBestPlayTime(),
                            rankingEntity.getUpdatedAt()
                    );
                    return RankingResponse.of(rankingEntity, (int) higherCount + 1);
                })
                .orElseGet(() -> RankingResponse.unranked(nickname));
    }

    // 내 랭킹 조회
    public RankingResponse getMyRanking(String uid) {
        UserEntity userEntity = userRepository.findByUid(uid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return rankingRepository.findByUserId(userEntity.getId())
                .map(rankingEntity -> {
                    long higherCount = rankingRepository.calculateMyRank(
                            rankingEntity.getBestPlayTime(),
                            rankingEntity.getUpdatedAt()
                    );
                    return RankingResponse.of(rankingEntity, (int) higherCount + 1);
                })
                .orElseGet(() -> RankingResponse.unranked(userEntity.getNickname()));
    }

    // 전적이 저장될 때마다 호출될 랭킹 갱신 로직
    @Transactional
    public void updatePlayTimeIfBest(UserEntity userEntity, int newScore) {

        rankingRepository.findByUserId(userEntity.getId()).ifPresentOrElse(
                // 1. 이미 랭킹 기록이 있는 유저 -> 기존 시간보다 짧을 때만 갱신
                rankingEntity -> {
                    if (newScore < rankingEntity.getBestPlayTime()) {
                        rankingEntity.updateBestPlayTime(newScore);
                    }
                },
                // 2. 랭킹 기록이 아예 없는 유저 (첫 클리어) -> 새로 만들기
                () -> {
                    RankingEntity newRankingEntity = RankingEntity.builder()
                            .userEntity(userEntity)
                            .bestPlayTime(newScore)
                            .build();
                    rankingRepository.save(newRankingEntity);
                }
        );
    }
}
