package clickme.clickme.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HeartRedisRepository implements HeartRepository {

    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public void increaseCount(String id) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.increment(id);
    }

    @Override
    public void add(String id) {
        redisTemplate.opsForValue().set(id, 0L);
    }

    public Long findById(String id) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        Long count = valueOperations.get(id);
        return count == null ? 0L : count;
    }
}
