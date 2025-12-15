package com.capstone.game_backend.domain.record.service;

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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final UserRepository userRepository;
    private final RecordRepository recordRepository;

    //생성
    public RecordResponse create(RecordCreateRequest req){
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Record record = Record.builder()
                .user(user)
                .score(req.getScore())
                .gameMeta(req.getGameMeta())
                .playedAt(LocalDateTime.now())
                .build();

        recordRepository.save(record);

        return new RecordResponse(
                record.getId(),
                record.getScore(),
                record.getGameMeta(),
                record.getPlayedAt()
        );
    }

    // 전적조회
    public List<RecordResponse> getRecords(String nickname){

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return recordRepository.findByUserIdOrderByPlayedAtDesc(user.getId())
                .stream()
                .map(r -> new RecordResponse(r.getId(), r.getScore(), r.getGameMeta(), r.getPlayedAt()))
                .toList();
    }
}
