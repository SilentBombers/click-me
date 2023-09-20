package clickme.clickme.controller.api;

import clickme.clickme.service.EmojiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiHeartController {

    private final EmojiService emojiService;

    @GetMapping(value = "/count", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> findClickCount(@RequestParam String id) throws IOException {
        CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.SECONDS);
        return ResponseEntity.ok()
                .cacheControl(cacheControl)
                .body(emojiService.heart(id));
    }
}
