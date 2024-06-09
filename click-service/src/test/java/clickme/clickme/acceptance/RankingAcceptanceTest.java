package clickme.clickme.acceptance;

import clickme.clickme.ranking.ui.response.RankingResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RankingAcceptanceTest extends AcceptanceTest {

    @Test
    void 특정_사용자의_랭킹을_조회한다() {
        String name = "seungpang";
        rankingRepository.add("seungpang");

        final ExtractableResponse<Response> response = 개인_랭킹_조회(name);
        final Long rank = response.as(Long.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(rank).isNotNull()
        );
    }

    @Test
    void 라이브_랭킹을_조회한다() {
        rankingRepository.increaseCount("seungpang");
        rankingRepository.increaseCount("seungpang");
        rankingRepository.increaseCount("seungpang");
        rankingRepository.increaseCount("angie");
        rankingRepository.increaseCount("angie");
        rankingRepository.increaseCount("chunsik");
        final ExtractableResponse<Response> response = 실시간_랭킹_조회(0, 2);
        final List<RankingResponse> rankings = response.jsonPath().getList(".", RankingResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(rankings.size()).isEqualTo(3),
                () -> assertThat(rankings.get(0).nickname()).isEqualTo("seungpang"),
                () -> assertThat(rankings.get(1).nickname()).isEqualTo("angie"),
                () -> assertThat(rankings.get(2).nickname()).isEqualTo("chunsik")
        );
    }

    @Test
    void 존재하지_않는_사용자의_랭킹을_조회하면_0을_반환한다() {
        String name = "unknown";

        final ExtractableResponse<Response> response = 개인_랭킹_조회(name);
        final Long rank = response.as(Long.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(rank).isEqualTo(0L)
        );
    }

    @Test
    void 빈_랭킹_범위_요청시_빈_리스트를_반환한다() {
        final ExtractableResponse<Response> response = 실시간_랭킹_조회(2, 2);
        final List<RankingResponse> rankings = response.jsonPath().getList(".", RankingResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(rankings).isEmpty()
        );
    }

    @Test
    void 랭킹_범위가_실제_랭킹_수를_초과하는_경우() {
        final ExtractableResponse<Response> response = 실시간_랭킹_조회(0, 10);
        final List<RankingResponse> rankings = response.jsonPath().getList(".", RankingResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(rankings.size()).isLessThanOrEqualTo(10)
        );
    }

    @Test
    void 랭킹_범위가_음수인_경우_빈_리스트를_반환한다() {
        final ExtractableResponse<Response> response = 실시간_랭킹_조회(-1, -1);
        final List<RankingResponse> rankings = response.jsonPath().getList(".", RankingResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(rankings).isEmpty()
        );
    }

    private static ExtractableResponse<Response> 개인_랭킹_조회(final String name) {
        return AcceptanceFixture.get("/api/v1/rankings/"  + name);
    }

    private static ExtractableResponse<Response> 실시간_랭킹_조회(int startRank, int endRank) {
        return AcceptanceFixture.get("/api/v1/rankings/live?startRank=" + startRank + "&endRank=" + endRank);
    }
}
