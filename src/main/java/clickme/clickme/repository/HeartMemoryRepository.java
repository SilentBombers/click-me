package clickme.clickme.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
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
}
