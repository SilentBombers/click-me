package clickme.clickme.repository;

import clickme.clickme.config.RedisConnectionCondition;
import clickme.clickme.controller.api.response.RankingResponse;
import clickme.clickme.repository.dto.RankingDto;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Conditional(RedisConnectionCondition.class)
public class HeartRedisRepository implements HeartRepository {

    private static final String RANKING_KEY = "clicks";
    private static final String CHANGED_KEY = "clickCountChanged%s";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ZSetOperations<String, String> rankings;
    private final SetOperations<String, String> changedMembers;

    public HeartRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.rankings = redisTemplate.opsForZSet();
        this.changedMembers = redisTemplate.opsForSet();
    }

    @Override
    public void increaseCount(final String id) {
        rankings.incrementScore(RANKING_KEY, id, 1);
    }

    @Override
    public void add(final String id) {
        rankings.add(RANKING_KEY, id, 0);
    }

    @Override
    public void saveChanged(final String id) {
        changedMembers.add(CHANGED_KEY.formatted(LocalDateTime.now().format(formatter)), id);
    }

    @Override
    public Long findById(final String id) {
        Double count = rankings.score(RANKING_KEY, id);
        return count == null ? 0L : count.longValue();
    }

    @Override
    public Long findRankByClicks(final String id) {
        return rankings.reverseRank(RANKING_KEY, id) + 1;
    }

    @Override
    public List<RankingResponse> findRealTimeRanking(final int start, final int end) {
        final AtomicLong ranking = new AtomicLong(start + 1);

        return rankings.reverseRangeWithScores(RANKING_KEY, start, end)
                .stream()
                .map(tuple -> new RankingDto(ranking.getAndIncrement(), tuple.getValue(), tuple.getScore().longValue()))
                .toList();
    }
}
