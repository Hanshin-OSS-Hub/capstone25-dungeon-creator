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

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final UserRepository userRepository;

    // 랭킹 전체 조회
    public List<RankingResponse> getAllRanking(){
        return rankingRepository.findAllByOrderByRankPositionAsc()
                .stream()
                .map(r -> new RankingResponse(
                        r.getUser().getNickname(),
                        r.getRankPosition(),
                        r.getRankScore()
                )).toList();
    }

    // 본인 랭킹 조회
    public RankingResponse getMyRanking(String nickname){

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Ranking ranking = rankingRepository.findByuserId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new RankingResponse(
                ranking.getUser().getNickname(),
                ranking.getRankPosition(),
                ranking.getRankScore()
        );
    }
}
