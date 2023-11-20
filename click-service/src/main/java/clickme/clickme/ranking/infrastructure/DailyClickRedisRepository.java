package clickme.clickme.ranking.infrastructure;

import clickme.clickme.config.RedisConnectionCondition;
import clickme.clickme.ranking.domain.DailyClickRepository;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
@Conditional(RedisConnectionCondition.class)
public class DailyClickRedisRepository implements DailyClickRepository {

    private static final String DAILY_CLICK_COUNT_KEY = "%s:dailyClickCount";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ZSetOperations<String, String> dailyClickCounts;

    public DailyClickRedisRepository(final RedisTemplate<String, String> redisTemplate) {
        this.dailyClickCounts = redisTemplate.opsForZSet();
    }

    @Override
    public void increaseCount(final String name) {
        final String key = generateKey();
        dailyClickCounts.incrementScore(key, name, 1);
    }

    @Override
    public void add(final String name) {
        final String key = generateKey();
        dailyClickCounts.add(key, name, 0);
    }

    private String generateKey() {
        return DAILY_CLICK_COUNT_KEY.formatted(LocalDateTime.now().format(formatter));
    }
}
