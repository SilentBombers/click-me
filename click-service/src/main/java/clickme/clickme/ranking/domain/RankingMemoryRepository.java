package clickme.clickme.ranking.domain;

import clickme.clickme.common.EntityNotFoundException;
import clickme.clickme.common.ErrorCode;
import clickme.clickme.ranking.infrastructure.dto.RankingDto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RankingMemoryRepository implements RankingRepository {

    private Map<String, Long> rankings =  new ConcurrentHashMap<>();

    @Override
    public void increaseCount(final String id) {
        rankings.put(id, rankings.getOrDefault(id, 0L) + 1);
    }

    @Override
    public void add(final String id) {
        rankings.put(id, 0L);
    }

    @Override
    public Long findByName(final String id) {
        return rankings.getOrDefault(id, 0L);
    }

    @Override
    public Long findRankByName(final String id) {
        Long value = rankings.get(id);
        if (value == null) {
            throw new EntityNotFoundException("Member not found", ErrorCode.INVALID_INPUT_VALUE);
        }

        long rank = 1;
        for (Map.Entry<String, Long> entry : rankings.entrySet()) {
            if (entry.getValue() > value) {
                rank++;
            }
        }

        return rank;
    }

    public List<RankingDto> findLiveRanking(final int start, final int end) {
        final AtomicLong ranking = new AtomicLong(start + 1);
        return rankings.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(entry -> new RankingDto(ranking.getAndIncrement(), entry.getKey(), entry.getValue()))
                .toList()
                .subList(start, end);
    }
}
