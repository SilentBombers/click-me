package clickme.transferservice.job.member;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

public class RedisRangePartitioner implements Partitioner {

    private final String key;
    private final RedisTemplate<String, String> redisTemplate;

    public RedisRangePartitioner(String key, RedisTemplate<String, String> redisTemplate) {
        this.key = key;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Long size = redisTemplate.opsForZSet().size(key);
        size = size == null ? 0L : size;

        if (size <= gridSize) {
            return createPartitionForSmallData(size);
        }
        return createPartitionForLargeData(size, gridSize);
    }

    private Map<String, ExecutionContext> createPartitionForSmallData(long size) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        for (int i = 0; i < size; i++) {
            partitions.put("partition" + i, createPartitionRange(i, i));
        }
        return partitions;
    }

    private Map<String, ExecutionContext> createPartitionForLargeData(long size, int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        long range = size / gridSize;
        long extra = size % gridSize;

        long startOffset = 0;
        for (int partitionIndex = 0; partitionIndex < gridSize; partitionIndex++) {
            long endOffset = startOffset + range - 1 + (partitionIndex < extra ? 1 : 0);
            partitions.put("partition" + partitionIndex, createPartitionRange(startOffset, endOffset));
            startOffset = endOffset + 1;
        }
        return partitions;
    }

    private ExecutionContext createPartitionRange(long startOffset, long endOffset) {
        ExecutionContext partitionRange = new ExecutionContext();
        partitionRange.putLong("startOffset", startOffset);
        partitionRange.putLong("endOffset", endOffset);
        return partitionRange;
    }
}
