package clickme.transferservice.job.member;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

@Configuration
public class MemberUpsertJobConfig {

    private final RedisTemplate<String, String> redisTemplate;

    public MemberUpsertJobConfig(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean
    @StepScope
    public ItemStreamReader<TypedTuple<String>> reader() {
        return new RedisCursorItemReader("clicks", redisTemplate);
    }
}
