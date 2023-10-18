package clickme.clickme.controller.api.response;

import clickme.clickme.domain.Rank;

public record RankResponse(Long rank, String nickname, Long count) {

    public static RankResponse mapToResponse(Rank rank) {
        return new RankResponse(rank.rank(), rank.nickname(), rank.count());
    }
}
