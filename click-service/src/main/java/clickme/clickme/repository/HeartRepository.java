package clickme.clickme.repository;

import java.util.Set;

public interface HeartRepository {

    void increaseCount(final String id);

    void add(final String id);

    Long findById(final String id);

    Long findRankByClicks(final String id);

    Set<String> findRealTimeRanking(final int start, final int end);
}
