package clickme.transferservice.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
public class HeartRedisRepository implements HeartRepository {

    private static final String KEY = "clicks";

    private final ZSetOperations<String, String> rankings;

    public HeartRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.rankings = redisTemplate.opsForZSet();
    }

    @Override
    public Long getClickCount(final String nickname) {
        return rankings.score(KEY, nickname)
                .longValue();
    }
}
