package clickme.clickme.repository;

import clickme.clickme.config.RedisConnectionCondition;
import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@Conditional(RedisConnectionCondition.class)
public class HeartRedisRepository implements HeartRepository {

    private static final String TOPIC = "click:count:";

    private final RedisTemplate<String, Long> redisTemplate;
    private ValueOperations<String, Long> valueOperations;

    public HeartRedisRepository(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void increaseCount(String id) {
        valueOperations.increment(createTopic(id));
    }

    @Override
    public void add(String id) {
        valueOperations.set(createTopic(id), 0L);
    }

    public Long findById(String id) {
        Long count = valueOperations.get(createTopic(id));
        return count == null ? 0L : count;
    }

    private String createTopic(String id) {
        return TOPIC + id;
    }
}
