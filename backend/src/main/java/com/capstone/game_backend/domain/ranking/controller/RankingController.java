package com.capstone.game_backend.domain.ranking.controller;

import com.capstone.game_backend.domain.ranking.dto.RankingResponse;
import com.capstone.game_backend.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/all")
    public List<RankingResponse> getAllRanking(){
        return rankingService.getAllRanking();
    }

    @GetMapping("/me")
    public RankingResponse getMyRanking(@RequestParam String nickname){
        return rankingService.getMyRanking(nickname);
    }
}
