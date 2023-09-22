package clickme.clickme.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(HeartRepository.class)
public class HeartMemoryRepository implements HeartRepository {

    private static final Map<String, Long> MAP =  new ConcurrentHashMap<>();

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
    public Set<String> findRealTimeRanking(int start, int end) {
        List<Map.Entry<String, Long>> sortedEntries = MAP.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        List<Map.Entry<String, Long>> sublist = sortedEntries.subList(start - 1, end);

        // 키만 추출하여 세트에 넣습니다.
        return sublist.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
