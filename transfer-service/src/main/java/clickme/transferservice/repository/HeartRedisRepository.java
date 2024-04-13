package clickme.transferservice.repository;

import clickme.transferservice.util.RedisKeyGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
public class HeartRedisRepository implements HeartRepository {

    private final ZSetOperations<String, String> rankings;

    public HeartRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.rankings = redisTemplate.opsForZSet();
    }

    @Override
    public Long getClickCount(final String nickname) {
        return rankings.score(RedisKeyGenerator.getCLickCountKey(), nickname) == null ?
                0L : rankings.score(RedisKeyGenerator.getCLickCountKey(), nickname).longValue();
    }
}
