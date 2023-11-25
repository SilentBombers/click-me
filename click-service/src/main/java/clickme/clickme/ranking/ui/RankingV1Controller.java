package clickme.clickme.ranking.ui;

import clickme.clickme.ranking.application.RankingService;
import clickme.clickme.ranking.ui.response.RankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rankings")
public class RankingV1Controller {

    private final RankingService rankingService;

    @GetMapping("/{name}")
    public ResponseEntity<Long> findRankByName(@PathVariable final String name) {
        return ResponseEntity.ok()
                .body(rankingService.findRankByName(name));
    }

    @GetMapping("/live")
    public ResponseEntity<List<RankingResponse>> findLiveRanking(@RequestParam final int startRank,
                                                                 @RequestParam final int endRank) {
        return ResponseEntity.ok()
                .body(rankingService.findLiveRanking(startRank, endRank));
    }
}
