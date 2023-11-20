package clickme.clickme.ranking.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DailyClickMemoryRepository implements DailyClickRepository {

    private Map<String, Long> dailyClickCounts =  new ConcurrentHashMap<>();

    @Override
    public void increaseCount(final String name) {
        dailyClickCounts.put(name, dailyClickCounts.get(name) + 1);
    }

    @Override
    public void add(final String name) {
        dailyClickCounts.put(name, 0L);
    }
}
