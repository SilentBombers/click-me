package clickme.clickme.history.ui;

import clickme.clickme.history.application.ClickCountHistoryService;
import clickme.clickme.history.application.dto.ClickCountHistoriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/daily-click-count")
public class DailyClickCountV1Controller {

    private final ClickCountHistoryService clickCountHistoryService;

    @GetMapping("/{name}")
    public ResponseEntity<ClickCountHistoriesResponse> findClickCountHistoryByNameAndDateBetween(
            @PathVariable final String name
    ) {
        return ResponseEntity.ok()
                .body(clickCountHistoryService.findClickCountHistoryByNameAndDateBetween(name));
    }
}
