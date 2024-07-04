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
    void 클릭카운트_히스토리를_정상적으로_조회한다() {
        String name = "testUser";
        LocalDate localDate = LocalDate.now().minusDays(2);
        clickCountHistoryRepository.save(new ClickCountHistory(name, localDate, 10L));
        ClickCountHistoryResponse expectedResponse = new ClickCountHistoryResponse(localDate, 10L);

        ClickCountHistoriesResponse clickCountHistoryResponses =
                clickCountHistoryService.findClickCountHistoryByNameAndDateBetween(name);

        assertThat(clickCountHistoryResponses.clickCountHistories().contains(expectedResponse)).isTrue();
    }

    @Test
    void 클릭카운트_히스토리가_존재하지_않으면_클릭카운트는_전부0이다() {
        String name = "testUser";
        ClickCountHistoriesResponse clickCountHistoryResponses =
                clickCountHistoryService.findClickCountHistoryByNameAndDateBetween(name);

        for (int i = 0; i < 7; i++) {
            assertThat(clickCountHistoryResponses.clickCountHistories().get(i).clickCount()).isZero();
        }
    }
}
