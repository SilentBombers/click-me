package clickme.transferservice.job.member;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class RedisPagingItemReader implements ItemStreamReader<String> {

    private static final int MEMBER_COUNT = 1000;
    private static final double SCORE_INCREMENT = 1;
    private static final double SCORE_MAX = Double.MAX_VALUE;

    private final String key;
    private final RedisTemplate<String, String> redisTemplate;
    private double lastScore = 0;
    private boolean hasReadBefore = false;
    private Queue<String> items = new LinkedList<>();

    public RedisPagingItemReader(String key, RedisTemplate<String, String> redisTemplate) {
        System.out.println(key);
        this.key = key;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String read() {
        if (items.isEmpty()) {
            fetchNextPage();
        }
        return items.poll();
    }

    private void fetchNextPage() {
        Set<String> page = getItemsByScoreRange();
        if (!page.isEmpty()) {
            String lastItem = getLastItemFromPage(page);
            updateLastScore(lastItem);
            items.addAll(page);
        }
    }

    private Set<String> getItemsByScoreRange() {
        double minScore = hasReadBefore ? lastScore + SCORE_INCREMENT : lastScore;
        return redisTemplate.opsForZSet().rangeByScore(key, minScore, SCORE_MAX, 0, MEMBER_COUNT);
    }

    private String getLastItemFromPage(Set<String> page) {
        return page.stream().reduce((first, second) -> second).orElse(null);
    }

    private void updateLastScore(String item) {
        if (item != null) {
            lastScore = redisTemplate.opsForZSet().score(key, item);
            hasReadBefore = true;
        }
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (executionContext.containsKey("lastScore")) {
            lastScore = executionContext.getDouble("lastScore");
            hasReadBefore = (boolean) executionContext.get("hasReadBefore");
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        executionContext.putDouble("lastScore", lastScore);
        executionContext.put("hasReadBefore", hasReadBefore);
    }
}
