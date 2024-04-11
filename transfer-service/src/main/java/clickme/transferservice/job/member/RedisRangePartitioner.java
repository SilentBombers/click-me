package clickme.transferservice.job.member;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

public class RedisRangePartitioner implements Partitioner {

    private final int partitionSize;
    private final String key;
    private final RedisTemplate<String, String> redisTemplate;

    public RedisRangePartitioner(String key, int partitionSize, RedisTemplate<String, String> redisTemplate) {
        this.key = key;
        this.partitionSize = partitionSize;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        long size = redisTemplate.opsForZSet().size(key); // Redis Sorted Set의 전체 사이즈를 가져옴
        if (size <= gridSize) {
            // 전체 데이터 사이즈가 gridSize 이하인 경우, 각 데이터에 대해 하나의 파티션만 생성
            return createPartitionForSmallData(size);
        } else {
            // 그렇지 않으면, 기존 로직대로 처리
            return createPartitionForLargeData(size, gridSize);
        }
    }

    private Map<String, ExecutionContext> createPartitionForSmallData(long size) {
        Map<String, ExecutionContext> result = new HashMap<>();
        for (int i = 0; i < size; i++) {
            ExecutionContext value = new ExecutionContext();
            value.putLong("startOffset", i);
            value.putLong("endOffset", i);
            result.put("partition" + i, value);
        }
        return result;
    }

    private Map<String, ExecutionContext> createPartitionForLargeData(long size, int gridSize) {
        long range = size / gridSize;
        long remainder = size % gridSize;
        Map<String, ExecutionContext> result = new HashMap<>();

        long startOffset = 0;
        long endOffset = range - 1;
        for (int i = 0; i < gridSize; i++) {
            if (i < remainder) {
                endOffset++;
            }

            ExecutionContext value = new ExecutionContext();
            value.putLong("startOffset", startOffset);
            value.putLong("endOffset", endOffset);
            result.put("partition" + i, value);

            System.out.println("startOffset = " + startOffset + " endOffset = " + endOffset);

            startOffset = endOffset + 1;
            endOffset += range;
        }
        return result;
    }
}
