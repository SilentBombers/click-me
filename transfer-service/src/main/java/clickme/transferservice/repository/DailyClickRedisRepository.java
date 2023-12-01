package clickme.transferservice.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DailyClickRedisRepository implements DailyClickRepository {

    private static final String DAILY_CLICK_COUNT_KEY = "%s:dailyClickCount";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ZSetOperations<String, String> dailyClickCounts;
    private final RedisTemplate<String, String> template;

    public DailyClickRedisRepository(final RedisTemplate<String, String> redisTemplate) {
        this.dailyClickCounts = redisTemplate.opsForZSet();
        this.template = redisTemplate;
    }

    @Override
    public Long getClickCount(final String name) {
        return dailyClickCounts.score(generateKey(), name)
                .longValue();
    }

    @Override
    public void deleteKey(final String key) {
        template.delete(key);
    }

    private String generateKey() {
        return DAILY_CLICK_COUNT_KEY.formatted(LocalDateTime.now().format(formatter));
    }
}
