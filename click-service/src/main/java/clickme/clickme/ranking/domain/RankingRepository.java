package clickme.clickme.ranking.domain;

import clickme.clickme.ranking.infrastructure.dto.RankingDto;

import java.util.List;

public interface RankingRepository {

    void increaseCount(final String name);

    void add(final String name);

    Long findByName(final String name);

    Long findRankByName(final String name);

    List<RankingDto> findLiveRanking(final int start, final int end);
}
