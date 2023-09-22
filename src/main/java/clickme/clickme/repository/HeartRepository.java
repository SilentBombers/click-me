package clickme.clickme.repository;

import java.util.Set;

public interface HeartRepository {

    void increaseCount(String id);

    void add(String id);

    Long findById(String id);

    Long findRankByClicks(String id);

    Set<String> findRealTimeRanking(int start, int end);
}
