package clickme.clickme.repository;

import clickme.clickme.config.RedisConnectionCondition;
import clickme.clickme.controller.api.response.RankingResponse;
import clickme.clickme.repository.dto.RankingDto;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
    public List<RankingDto> findRealTimeRanking(final int start, final int end) {
        final AtomicLong ranking = new AtomicLong(start);
        return zSet.reverseRangeWithScores(KEY, start, end)
                .stream()
                .map(tuple -> new RankingDto(ranking.getAndIncrement(), tuple.getValue(), tuple.getScore().longValue()))
                .toList();
    }
}
