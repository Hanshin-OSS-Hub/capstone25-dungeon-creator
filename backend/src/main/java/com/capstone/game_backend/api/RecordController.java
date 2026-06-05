package com.capstone.game_backend.api;

import com.capstone.game_backend.domain.record.dto.RecordCreateRequest;
import com.capstone.game_backend.domain.record.dto.RecordResponse;
import com.capstone.game_backend.domain.record.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public RecordResponse create(
            // JWT 필터를 통과한 유저의 uid
            @AuthenticationPrincipal String uid,
            @Valid @RequestBody RecordCreateRequest req){
        return recordService.create(uid, req);
    }

    // 최근 플레이한 전적 순서대로 검색 (실패 기록 포함)
    // ("/recent") 추가
    @GetMapping
    public List<RecordResponse> getRecentList(@RequestParam String nickname){
        return recordService.getRecentRecords(nickname);
    }

    // 클리어 타임이 빠른 순서대로 검색 (성공 기록만)
    @GetMapping("/best")
    public List<RecordResponse> getBestList(@RequestParam String nickname){
        return recordService.getBestRecords(nickname);
    }
}
