package clickme.clickme.repository;

import clickme.clickme.controller.api.response.RankingResponse;

import java.util.List;

public interface HeartRepository {

    void increaseCount(final String id);

    void add(final String id);

    void saveChanged(final String id);

    Long findById(final String id);

    Long findRankByClicks(final String id);

    List<RankingResponse> findRealTimeRanking(final int start, final int end);
}
