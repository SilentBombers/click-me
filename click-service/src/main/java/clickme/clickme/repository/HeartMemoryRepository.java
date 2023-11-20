package clickme.clickme.repository;

import clickme.clickme.controller.api.response.RankingResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

public class HeartMemoryRepository implements HeartRepository {

    private Map<String, Long> rankings =  new ConcurrentHashMap<>();
    private Set<String> changedMembers = new CopyOnWriteArraySet();

    @Override
    public void increaseCount(final String id) {
        rankings.put(id, rankings.get(id) + 1);
    }

    @Override
    public void add(final String id) {
        rankings.put(id, 0L);
    }

    @Override
    public void saveChanged(final String id) {
        changedMembers.add(id);
    }

    @Override
    public Long findById(final String id) {
        return rankings.getOrDefault(id, 0L);
    }

    @Override
    public Long findRankByClicks(final String id) {
        Long value = rankings.get(id);
        if (value == null) {
            throw new NoSuchElementException("해당 id로 등록된 사용자가 없습니다: " + id);
        }

        long rank = 1;
        for (Map.Entry<String, Long> entry : rankings.entrySet()) {
            if (entry.getValue() > value) {
                rank++;
            }
        }

        return rank;
    }

    public List<RankingResponse> findRealTimeRanking(final int start, final int end) {
        final AtomicLong ranking = new AtomicLong(start + 1);
        return rankings.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(entry -> new RankingResponse(ranking.getAndIncrement(), entry.getKey(), entry.getValue()))
                .toList()
                .subList(start, end);
    }
}
