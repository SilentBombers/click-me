package clickme.clickme.acceptance;

import clickme.clickme.history.application.dto.ClickCountHistoriesResponse;
import clickme.clickme.history.application.dto.ClickCountHistoryResponse;
import clickme.clickme.history.domain.ClickCountHistory;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HistoryAcceptanceTest extends AcceptanceTest {

    @Test
    void 당일_클릭카운트가_존재하지_않고_기록들도_없다면_클릭카운트는_전부_0이다() {
        String name = "seungpang";

        final ExtractableResponse<Response> response = AcceptanceFixture.get("api/v1/daily-click-count/" + name);
        final ClickCountHistoriesResponse actual = response.as(ClickCountHistoriesResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actual.clickCountHistories().size()).isEqualTo(7),
                () -> assertThat(actual.clickCountHistories().stream().allMatch(history -> history.clickCount() == 0L)).isTrue()
        );
    }

    @Test
    void 전날_클릭카운트가10이면_히스토리_마지막도_10이다() {
        String name = "seungpang";
        final LocalDate localDate = LocalDate.now().minusDays(1);
        clickCountHistoryRepository.save(new ClickCountHistory(name, localDate, 10L));

        final ExtractableResponse<Response> response = AcceptanceFixture.get("api/v1/daily-click-count/" + name);
        final ClickCountHistoriesResponse actual = response.as(ClickCountHistoriesResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actual.clickCountHistories().get(6).clickCount()).isEqualTo(10)
        );
    }

    @Test
    void 일부_날짜에_대한_클릭_카운트_기록이_없는_경우_누락된_날짜에_대한_기록은_0으로_채워진다() {
        String name = "seungpang";
        LocalDate today = LocalDate.now();
        clickCountHistoryRepository.save(new ClickCountHistory(name, today.minusDays(7), 10L));
        clickCountHistoryRepository.save(new ClickCountHistory(name, today.minusDays(5), 5L));

        final ExtractableResponse<Response> response = AcceptanceFixture.get("api/v1/daily-click-count/" + name);
        final ClickCountHistoriesResponse historiesResponse = response.as(ClickCountHistoriesResponse.class);
        final List<ClickCountHistoryResponse> actual = historiesResponse.clickCountHistories();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actual.get(0).clickCount()).isEqualTo(10),
                () -> assertThat(actual.get(2).clickCount()).isEqualTo(5),
                () -> assertThat(actual.get(4).clickCount()).isZero()
        );
    }

    @Test
    void 존재하지_않는_사용자의_클릭_카운트는_전부_0으로_반환된다() {
        String name = "unknownUser";

        final ExtractableResponse<Response> response = AcceptanceFixture.get("api/v1/daily-click-count/" + name);
        final ClickCountHistoriesResponse historiesResponse = response.as(ClickCountHistoriesResponse.class);
        final List<ClickCountHistoryResponse> actual = historiesResponse.clickCountHistories();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actual.size()).isEqualTo(7),
                () -> assertThat(actual.stream().allMatch(history -> history.clickCount() == 0L)).isTrue()
        );
    }

    @Test
    void 경계_조건_7일_이전의_데이터는_반환되지_않는다() {
        String name = "seungpang";
        LocalDate today = LocalDate.now();
        clickCountHistoryRepository.save(new ClickCountHistory(name, today.minusDays(8), 20L));
        clickCountHistoryRepository.save(new ClickCountHistory(name, today.minusDays(7), 15L));

        final ExtractableResponse<Response> response = AcceptanceFixture.get("api/v1/daily-click-count/" + name);
        final ClickCountHistoriesResponse actual = response.as(ClickCountHistoriesResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.clickCountHistories().stream().noneMatch(history -> history.clickCount() == 20L)).isTrue();
    }
}
