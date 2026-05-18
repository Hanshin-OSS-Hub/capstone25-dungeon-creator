package com.capstone.game_backend.domain.api;

import com.capstone.game_backend.domain.ranking.RankingResponse;
import com.capstone.game_backend.domain.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/top100")
    public List<RankingResponse> getTop10Ranking(){
        return rankingService.getTop100Ranking();
    }


    @GetMapping("/search")
    public RankingResponse getRankingByNickname(@RequestParam String nickname){
        return rankingService.getRankingByNickname(nickname);
    }

    @GetMapping("/me")
    public RankingResponse getMyRanking(@AuthenticationPrincipal String uid) {
        return rankingService.getMyRanking(uid);
    }
}
