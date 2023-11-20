package clickme.transferservice.job.member;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReaderException;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.Iterator;
import java.util.Set;

public class RedisCursorItemReader implements ItemStreamReader<String> {

    private final String key;
    private final SetOperations<String, String> setOperations;
    private Iterator<String> iterator;

    public RedisCursorItemReader(final String key, final RedisTemplate<String, String> redisTemplate) {
        this.key = key;
        this.setOperations = redisTemplate.opsForSet();
    }


    @Override
    public String read() throws ItemReaderException {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    @Override
    public void open(final ExecutionContext executionContext) throws ItemStreamException {
        Set<String> nicknames = setOperations.members(key);
        iterator = nicknames.iterator();
    }

    @Override
    public void update(final ExecutionContext executionContext) throws ItemStreamException {
        ItemStreamReader.super.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        ItemStreamReader.super.close();
    }
}
