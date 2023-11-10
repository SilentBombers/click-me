package clickme.clickme.repository;

import clickme.clickme.config.RedisConnectionCondition;
import clickme.clickme.controller.api.response.RankingResponse;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Conditional(RedisConnectionCondition.class)
public class HeartRedisRepository implements HeartRepository {

    private static final String KEY = "clicks";

    private final ZSetOperations<String, String> rankings;
    private final SetOperations<String, String> changedMembers;

    public HeartRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.rankings = redisTemplate.opsForZSet();
        this.changedMembers = redisTemplate.opsForSet();
    }

    @Override
    public void increaseCount(final String id) {
        rankings.incrementScore(KEY, id, 1);
    }

    @Override
    public void add(final String id) {
        rankings.add(KEY, id, 0);
    }

    @Override
    public void saveChanged(final String id) {
        changedMembers.add(KEY, id);
    }

    @Override
    public Long findById(final String id) {
        Double count = rankings.score(KEY, id);
        return count == null ? 0L : count.longValue();
    }

    @Override
    public Long findRankByClicks(final String id) {
        return rankings.reverseRank(KEY, id) + 1;
    }

    @Override
    public List<RankingResponse> findRealTimeRanking(final int start, final int end) {
        final AtomicLong ranking = new AtomicLong(start + 1);

        return rankings.reverseRangeWithScores(KEY, start, end)
                .stream()
                .map(tuple -> new RankingResponse(ranking.getAndIncrement(), tuple.getValue(), tuple.getScore().longValue()))
                .toList();
    }
}
