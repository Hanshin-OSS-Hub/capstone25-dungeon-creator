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

    @GetMapping
    public List<RecordResponse> list(@RequestParam String nickname){
        return recordService.getRecords(nickname);
    }
}
