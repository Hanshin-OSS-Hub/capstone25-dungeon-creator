package com.capstone.game_backend.domain.record;

import com.capstone.game_backend.domain.ranking.RankingService;
import com.capstone.game_backend.domain.record.dto.RecordCreateRequest;
import com.capstone.game_backend.domain.record.dto.RecordResponse;
import com.capstone.game_backend.domain.user.UserEntity;
import com.capstone.game_backend.domain.user.UserRepository;
import com.capstone.game_backend.global.error.CustomException;
import com.capstone.game_backend.global.error.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    //생성
    @Transactional
    public RecordResponse create(String uid, RecordCreateRequest req){

        UserEntity user = userRepository.findByUid(uid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean isCleared = false; // 기본값은 false

        try {
            // 1. JSON 문자열을 먼저 읽어서 클리어 여부 확인
            JsonNode metaNode = objectMapper.readTree(req.gameMeta());
            if (metaNode.has("isCleared") && metaNode.get("isCleared").asBoolean()) {
                isCleared = true;
            }
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 2. 파싱해서 얻어낸 isCleared 값을 엔티티에 함께 넣어서 생성
        RecordEntity record = RecordEntity.builder()
                .user(user)
                .gameMeta(req.gameMeta())
                .playTimeSeconds(req.playTimeSeconds())
                .isCleared(isCleared) // DB 컬럼에 저장
                .build();

        recordRepository.save(record);

        // 3. 클리어한 유저만 랭킹 반영 로직 실행
        if (isCleared) {
            rankingService.updatePlayTimeIfBest(user, req.playTimeSeconds());
        }

        return RecordResponse.from(record);
    }

    // 전적조회 1. 최고기록 순
    public List<RecordResponse> getBestRecords(String nickname){

        UserEntity user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return recordRepository.findByUser_IdAndIsClearedTrueOrderByPlayTimeSecondsAsc(user.getId())
                .stream()
                .map(RecordResponse::from)
                .toList();
    }

    // 2. 최근전적 순
    public List<RecordResponse> getRecentRecords(String nickname){
        UserEntity user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 최신순(CreatedAtDesc)으로 가져오기
        return recordRepository.findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(RecordResponse::from)
                .toList();
    }
}
