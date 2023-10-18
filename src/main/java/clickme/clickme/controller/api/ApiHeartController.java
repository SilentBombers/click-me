package clickme.clickme.controller.api;

import clickme.clickme.controller.api.response.RankingResponse;
import clickme.clickme.service.EmojiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clicks")
public class ApiHeartController {

    private final EmojiService emojiService;

    @GetMapping(value = "/count")
    public ResponseEntity<String> findClickCount(@RequestParam final String id) throws IOException, TransformerException {
        final CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.SECONDS);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .cacheControl(cacheControl)
                .body(emojiService.heart(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Long> findRankByClicks(@PathVariable final String id) {
        return ResponseEntity.ok()
                .body(emojiService.findRankByClicks(id));
    }

    @GetMapping("/realtime")
    public ResponseEntity<List<RankingResponse>> findRealTimeRanking(@RequestParam final long startRank,
                                                                     @RequestParam final long endRank) {
        return ResponseEntity.ok()
                        .body(emojiService.findRealTimeRanking(startRank, endRank));
    }
}
