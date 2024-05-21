package clickme.clickme.history.application;

import clickme.clickme.history.application.dto.ClickCountHistoriesResponse;
import clickme.clickme.history.application.dto.ClickCountHistoryResponse;
import clickme.clickme.history.domain.ClickCountHistory;
import clickme.clickme.history.domain.ClickCountHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ClickCountHistoryServiceTest {

    @Autowired
    private ClickCountHistoryService clickCountHistoryService;

    @Autowired
    private ClickCountHistoryRepository clickCountHistoryRepository;

    @Test
    void findClickCountHistoryByNameAndDateBetween_whenHistoryExists() {
        // Given
        String name = "testUser";
        LocalDate localDate = LocalDate.now();
        clickCountHistoryRepository.save(new ClickCountHistory(name, localDate, 10L));

        ClickCountHistoryResponse expectedResponse = new ClickCountHistoryResponse(localDate, 10L);

        // When
        ClickCountHistoriesResponse clickCountHistoryResponses =
                clickCountHistoryService.findClickCountHistoryByNameAndDateBetween(name);

        // Then
        assertThat(clickCountHistoryResponses.clickCountHistories().contains(expectedResponse)).isTrue();
    }

    @Test
    void findClickCountHistoryByNameAndDateBetween_whenHistoryDoesNotExist() {
        String name = "testUser";
        ClickCountHistoriesResponse clickCountHistoryResponses =
                clickCountHistoryService.findClickCountHistoryByNameAndDateBetween(name);

        // Then
        for (int i = 0; i < 7; i++) {
            assertThat(clickCountHistoryResponses.clickCountHistories().get(i).clickCount()).isZero();
        }
    }
}
