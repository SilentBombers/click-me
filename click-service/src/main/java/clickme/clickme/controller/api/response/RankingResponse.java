package clickme.clickme.controller.api.response;

import clickme.clickme.domain.Rank;

public record RankingResponse(int rank, String nickname, Long count) {

    public static RankingResponse mapToResponse(Rank rank) {
        return new RankingResponse(rank.rank(), rank.nickname(), rank.count());
    }
}
