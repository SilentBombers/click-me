package clickme.clickme.ranking.infrastructure;

import clickme.clickme.config.RedisConnectionCondition;
import clickme.clickme.ranking.domain.RankingRepository;
import clickme.clickme.ranking.infrastructure.dto.RankingDto;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Conditional(RedisConnectionCondition.class)
public class RankingRedisRepository implements RankingRepository {

    private static final String RANKING_KEY = "clicks";

    private final ZSetOperations<String, String> rankings;

    public RankingRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.rankings = redisTemplate.opsForZSet();
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
    public Long findByName(final String name) {
        Double count = rankings.score(RANKING_KEY, name);
        return count == null ? 0L : count.longValue();
    }

    @Override
    public Long findRankByName(final String name) {
        final Long rank = rankings.reverseRank(RANKING_KEY, name);
        return rank == null ? 0L : rank + 1;
    }

    @Override
    public List<RankingDto> findLiveRanking(final int start, final int end) {
        if (start < 0 || end < start) {
            return Collections.emptyList();
        }

        final AtomicLong ranking = new AtomicLong(start + 1L);

        Set<ZSetOperations.TypedTuple<String>> rangeWithScores
                = rankings.reverseRangeWithScores(RANKING_KEY, start, end);
        if (rangeWithScores == null) {
            return Collections.emptyList();
        }

        return rangeWithScores.stream()
                .map(tuple -> new RankingDto(ranking.getAndIncrement(), tuple.getValue(), tuple.getScore().longValue()))
                .toList();
    }
}
