package com.capstone.game_backend.domain.record.controller;

import com.capstone.game_backend.domain.record.dto.RecordCreateRequest;
import com.capstone.game_backend.domain.record.dto.RecordResponse;
import com.capstone.game_backend.domain.record.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {

    private final RecordService recordService;

    @PostMapping
    public RecordResponse create(@RequestBody RecordCreateRequest req){
        return recordService.create(req);
    }

    @GetMapping
    public List<RecordResponse> list(@RequestParam String nickname){
        return recordService.getRecords(nickname);
    }
}
