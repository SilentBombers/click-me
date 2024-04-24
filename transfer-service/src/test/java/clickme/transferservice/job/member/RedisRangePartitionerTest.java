package clickme.transferservice.job.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisRangePartitionerTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    private RedisRangePartitioner partitioner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        partitioner = new RedisRangePartitioner("testKey", redisTemplate);
    }

    @Test
    void gridSize에_맞게_offset이_분할된다() {
        long size = 10;
        int gridSize = 5;
        when(zSetOperations.size("testKey")).thenReturn(size);
        final Map<String, ExecutionContext> executionContextMap = partitioner.partition(gridSize);

        final ExecutionContext partition1 = executionContextMap.get("partition0");
        assertThat(partition1.getLong("startOffset")).isEqualTo(0L);
        assertThat(partition1.getLong("endOffset")).isEqualTo(1L);

        final ExecutionContext partition5 = executionContextMap.get("partition4");
        assertThat(partition5.getLong("startOffset")).isEqualTo(8L);
        assertThat(partition5.getLong("endOffset")).isEqualTo(9L);
    }

    @Test
    void 데이터가_1개인_경우_파티션도_1개다() {
        long size = 1;
        int gridSize = 5;
        when(zSetOperations.size("testKey")).thenReturn(size);
        final Map<String, ExecutionContext> executionContextMap = partitioner.partition(gridSize);

        assertThat(executionContextMap.size()).isOne();
    }
}
