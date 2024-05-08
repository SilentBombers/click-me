package clickme.transferservice.repository;

import clickme.transferservice.util.RedisKeyGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DailyClickRedisRepository implements DailyClickRepository {

    private final ZSetOperations<String, String> dailyClickCounts;
    private final RedisTemplate<String, String> template;

    public DailyClickRedisRepository(final RedisTemplate<String, String> redisTemplate) {
        this.dailyClickCounts = redisTemplate.opsForZSet();
        this.template = redisTemplate;
    }

    @Override
    public Long getClickCount(final String name) {
        return Objects.requireNonNull(dailyClickCounts.score(RedisKeyGenerator.getDailyClickCountKey(), name))
                .longValue();
    }

    @Override
    public void deleteKey(final String key) {
        template.delete(key);
    }
}
