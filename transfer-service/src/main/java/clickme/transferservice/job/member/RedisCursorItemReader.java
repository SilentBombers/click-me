package clickme.transferservice.job.member;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReaderException;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public class RedisCursorItemReader implements ItemStreamReader<TypedTuple<String>> {

    private static final int MEMBER_COUNT = 1000;

    private final String key;
    private final ZSetOperations<String, String> zSetOperations;
    private Cursor<TypedTuple<String>> cursor;

    public RedisCursorItemReader(final String key, final RedisTemplate<String, String> redisTemplate) {
        this.key = key;
        this.zSetOperations = redisTemplate.opsForZSet();
    }


    @Override
    public TypedTuple<String> read() throws ItemReaderException {
        if (cursor.hasNext()) {
            return cursor.next();
        }
        return null;
    }

    @Override
    public void open(final ExecutionContext executionContext) throws ItemStreamException {
        cursor = zSetOperations.scan(key, ScanOptions.scanOptions().count(MEMBER_COUNT).build());
    }
}
