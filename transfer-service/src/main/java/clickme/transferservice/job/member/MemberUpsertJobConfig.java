package clickme.transferservice.job.member;

import clickme.transferservice.domain.UpsertMember;
import clickme.transferservice.repository.HeartRepository;
import clickme.transferservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class MemberUpsertJobConfig {

    private static final int CHUCK_SIZE = 1000;
    private static final String REDIS_KEY = "clickCountChanged";
    private static final String STEP_NAME = "syncRedisToMysqlStep";
    private static final String JOB_NAME = "syncRedisToMysqlJob";

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;
    private final MemberUpsertAfterJobListener memberUpsertAfterJobListener;

    @Bean
    @StepScope
    public ItemStreamReader<String> reader() {
        return new RedisCursorItemReader(REDIS_KEY, redisTemplate);
    }

    @Bean
    @StepScope
    public ItemProcessor<String, UpsertMember> processor() {
        return nickname -> {
            Long clickCount = heartRepository.getClickCount(nickname);
            return new UpsertMember(nickname, clickCount);
        };
    }

    @Bean
    @StepScope
    public ItemWriter<UpsertMember> writer() {
        return new MysqlItemWriter(memberRepository);
    }

    @Bean
    @JobScope
    public Step syncRedisToMySqlStep(final ItemReader<String> reader,
                                     final ItemWriter<UpsertMember> writer,
                                     final JobRepository jobRepository,
                                     final PlatformTransactionManager transactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<String, UpsertMember>chunk(CHUCK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job syncRedisToMysqlJob(@Qualifier("syncRedisToMySqlStep") final Step syncRedisToMysqlStep,
                                   final JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(syncRedisToMysqlStep)
                .end()
                .listener(memberUpsertAfterJobListener)
                .build();
    }
}
