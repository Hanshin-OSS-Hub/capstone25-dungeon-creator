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

        // 1. 유저찾기
        User user = userRepository.findByUid(uid)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 엔티티 조립, 저장
        Record record = Record.builder()
                .user(user)
                .gameMeta(req.getGameMeta())
                .playTimeSeconds(req.getPlayTimeSeconds())
                .build();

        recordRepository.save(record);

        // 3. 클리어한 유저만 랭킹반영
        try {
            JsonNode metaNode = objectMapper.readTree(req.getGameMeta());

            if (metaNode.has("isCleared") && metaNode.get("isCleared").asBoolean()) {

                rankingService.updatePlayTimeIfBest(user, req.getPlayTimeSeconds());
            }
        } catch (Exception e) {
            // 클라이언트가 보낸 JSON이 깨졌거나 형식이 안 맞을 경우 예외 처리
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return RecordResponse.from(record);

    }

    // 전적조회
    public List<RecordResponse> getRecords(String nickname){

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return recordRepository.findByUserIdOrderByPlayTimeSecondsAsc(user.getId())
                .stream()
                .map(RecordResponse::from)
                .toList();
    }
}
