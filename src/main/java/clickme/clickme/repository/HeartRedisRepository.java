package clickme.clickme.repository;

import clickme.clickme.config.RedisConnectionCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@Conditional(RedisConnectionCondition.class)
public class HeartRedisRepository implements HeartRepository {

    private static final String KEY = "clicks";

    private final ZSetOperations<String, String> zSet;

    public HeartRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.zSet = redisTemplate.opsForZSet();
    }

    @Override
    public void increaseCount(final String id) {
        zSet.incrementScore(KEY, id, 1);
    }

    @Override
    public void add(final String id) {
        zSet.add(KEY, id, 0);
    }

    @Override
    public Long findById(final String id) {
        Double count = zSet.score(KEY, id);
        return count == null ? 0L : count.longValue();
    }

    @Override
    public Long findRankByClicks(final String id) {
        return zSet.reverseRank(KEY, id) + 1;
    }

    @Override
    public Set<String> findRealTimeRanking(final int start, final int end) {
        return zSet.reverseRange(KEY, start, end);
    }
}
