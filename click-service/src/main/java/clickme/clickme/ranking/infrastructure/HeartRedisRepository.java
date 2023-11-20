package clickme.clickme.ranking.infrastructure;

import clickme.clickme.config.RedisConnectionCondition;
import clickme.clickme.ranking.domain.HeartRepository;
import clickme.clickme.ranking.infrastructure.dto.RankingDto;
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
    public void increaseCount(final String name) {
        rankings.incrementScore(RANKING_KEY, name, 1);
    }

    @Override
    public void add(final String name) {
        rankings.add(RANKING_KEY, name, 0);
    }

    @Override
    public void saveChanged(final String name) {
        changedMembers.add(CHANGED_KEY.formatted(LocalDateTime.now().format(formatter)), name);
    }

    @Override
    public Long findByName(final String name) {
        Double count = rankings.score(RANKING_KEY, name);
        return count == null ? 0L : count.longValue();
    }

    @Override
    public Long findRankByName(final String name) {
        return rankings.reverseRank(RANKING_KEY, name) + 1;
    }

    @Override
    public List<RankingDto> findLiveRanking(final int start, final int end) {
        final AtomicLong ranking = new AtomicLong(start + 1);

        return rankings.reverseRangeWithScores(RANKING_KEY, start, end)
                .stream()
                .map(tuple -> new RankingDto(ranking.getAndIncrement(), tuple.getValue(), tuple.getScore().longValue()))
                .toList();
    }
}
