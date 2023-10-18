package clickme.clickme.repository;

import clickme.clickme.controller.api.response.RankingResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class HeartMemoryRepository implements HeartRepository {

    private Map<String, Long> MAP =  new ConcurrentHashMap<>();

    @Override
    public void increaseCount(String id) {
        MAP.put(id, MAP.get(id) + 1);
    }

    @Override
    public void add(String id) {
        MAP.put(id, 0L);
    }

    @Override
    public Long findById(String id) {
        return MAP.getOrDefault(id, 0L);
    }

    @Override
    public Long findRankByClicks(String id) {
        Long value = MAP.get(id);
        if (value == null) {
            throw new NoSuchElementException("해당 id로 등록된 사용자가 없습니다: " + id);
        }

        long rank = 1;
        for (Map.Entry<String, Long> entry : MAP.entrySet()) {
            if (entry.getValue() > value) {
                rank++;
            }
        }

        return rank;
    }

    @Override
    public List<RankingResponse> findRealTimeRanking(long start, long end) {
        final AtomicLong ranking = new AtomicLong(start);
        return MAP.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(entry -> new RankingResponse(ranking.getAndIncrement(), entry.getKey(), entry.getValue()))
                .toList()
                .subList((int) start, (int) end);
    }
}
