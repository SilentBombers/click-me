package clickme.transferservice.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
public class HeartRedisRepository implements HeartRepository {

    private static final String KEY = "clicks";

    private final ZSetOperations<String, String> rankings;
    private final RedisTemplate<String, String> template;

    public HeartRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.rankings = redisTemplate.opsForZSet();
        this.template = redisTemplate;
    }

    @Override
    public Long getClickCount(final String nickname) {
        return rankings.score(KEY, nickname)
                .longValue();
    }

    @Override
    public void deleteKey(final String key) {
        template.delete(key);
    }
}
