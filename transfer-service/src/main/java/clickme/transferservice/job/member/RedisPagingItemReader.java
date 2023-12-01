package clickme.transferservice.job.member;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.data.redis.core.*;

import java.util.Iterator;
import java.util.Set;

public class RedisPagingItemReader implements ItemStreamReader<String> {

    private static final int MEMBER_COUNT = 1000;

    private final String key;
    private final RedisTemplate<String, String> redisTemplate;
    private double lastScore = 0;

    public RedisPagingItemReader(String key, RedisTemplate<String, String> redisTemplate) {
        this.key = key;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String read() {
        Set<String> page = fetchNextPage();
        if (!page.isEmpty()) {
            String lastItem = page.iterator().next();
            lastScore = redisTemplate.opsForZSet().score(key, lastItem);
            return lastItem;
        }
        return null;
    }

    private Set<String> fetchNextPage() {
        return redisTemplate.opsForZSet().rangeByScore(key, lastScore, Double.MAX_VALUE, 0, MEMBER_COUNT);
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (executionContext.containsKey("lastScore")) {
            lastScore = executionContext.getDouble("lastScore");
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        executionContext.putDouble("lastScore", lastScore);
    }
}
