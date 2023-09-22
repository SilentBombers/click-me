package clickme.clickme.controller.api;

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

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clicks")
public class ApiHeartController {

    private final EmojiService emojiService;

    @GetMapping(value = "/count", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> findClickCount(@RequestParam String id) throws IOException {
        CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.SECONDS);
        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .body(emojiService.heart(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Long> findRankByClicks(@PathVariable String id) {
        return ResponseEntity.ok()
                .body(emojiService.findRankByClicks(id));
    }

    @GetMapping("/realtime")
    public ResponseEntity<Set<String>> findRealTimeRanking(@RequestParam int startRank, @RequestParam int endRank) {
        return ResponseEntity.ok()
                        .body(emojiService.findRealTimeRanking(startRank, endRank));
    }
}
