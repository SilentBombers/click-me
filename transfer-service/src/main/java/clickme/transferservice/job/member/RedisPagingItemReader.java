package clickme.transferservice.job.member;

import org.springframework.batch.item.ItemStreamReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class RedisPagingItemReader implements ItemStreamReader<TypedTuple<String>> {

    private static final int MEMBER_COUNT = 1000;

    private final String key;
    private final RedisTemplate<String, String> redisTemplate;
    private long startOffset = 0L;
    private long endOffset = Long.MAX_VALUE;
    private Queue<TypedTuple<String>> items = new LinkedList<>();

    public RedisPagingItemReader(final String key,
                                 final RedisTemplate<String, String> redisTemplate,
                                 final long startOffset,
                                 final long endOffset) {
        this.key = key;
        this.redisTemplate = redisTemplate;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public TypedTuple<String> read() {
        if (items.isEmpty()) {
            fetchNextPage();
        }
        return items.isEmpty() ?  null : items.poll();
    }

    private void fetchNextPage() {
        long nextPageEndOffset = Math.min(startOffset + MEMBER_COUNT - 1, endOffset);
        Set<TypedTuple<String>> page = redisTemplate.opsForZSet().rangeWithScores(key, startOffset, nextPageEndOffset);
        if (page != null && !page.isEmpty()) {
            items.addAll(page);
            startOffset += page.size(); // 페이지를 읽은 후 offset 업데이트
        }
    }
}

