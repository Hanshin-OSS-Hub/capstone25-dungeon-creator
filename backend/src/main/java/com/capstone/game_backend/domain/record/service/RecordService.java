package com.capstone.game_backend.domain.record.service;

import com.capstone.game_backend.domain.ranking.service.RankingService;
import com.capstone.game_backend.domain.record.dto.RecordCreateRequest;
import com.capstone.game_backend.domain.record.dto.RecordResponse;
import com.capstone.game_backend.domain.record.entity.Record;
import com.capstone.game_backend.domain.record.repository.RecordRepository;
import com.capstone.game_backend.domain.user.entity.User;
import com.capstone.game_backend.domain.user.repository.UserRepository;
import com.capstone.game_backend.global.error.CustomException;
import com.capstone.game_backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final RankingService rankingService;

    //생성
    @Transactional
    public RecordResponse create(String uid, RecordCreateRequest req){

        // 1. 유저찾기
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 엔티티 조립, 저장
        Record record = Record.builder()
                .user(user)
                .score(req.getScore())
                .gameMeta(req.getGameMeta())
                .clearTimeSeconds(req.getClearTimeSeconds())
                .build();

        recordRepository.save(record);

        // 3. 전적 저장이 끝났다면, 랭킹 보드에 최고 점수 갱신 요청
        rankingService.updateScoreIfBest(user, req.getScore());
        // Ranking 갱신 실패로 인한 Record 롤백의 가능성 문제 -> 이벤트 기반 비동기처리

        return RecordResponse.from(record);
    }

    // 전적조회
    public List<RecordResponse> getRecords(String nickname){

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return recordRepository.findByUserIdOrderByClearTimeSecondsAsc(user.getId())
                .stream()
                .map(RecordResponse::from)
                .toList();
    }
}
