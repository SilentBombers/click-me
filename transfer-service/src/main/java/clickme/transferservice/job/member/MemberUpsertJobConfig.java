package clickme.transferservice.job.member;

import clickme.transferservice.job.member.dto.DailyClickCount;
import clickme.transferservice.job.member.dto.UpsertMember;
import clickme.transferservice.repository.HeartRepository;
import clickme.transferservice.repository.MemberRepository;
import clickme.transferservice.util.RedisKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.batch.job.name", havingValue = "syncRedisToMysqlJob")
public class MemberUpsertJobConfig {

    private static final int POOL_SIZE = 10;
    private static final int PARTITION_SIZE = 10;
    private static final int CHUNK_SIZE = 1000;
    private static final String STEP_NAME = "syncRedisToMysqlStep";
    private static final String JOB_NAME = "syncRedisToMysqlJob";

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;
    private final MemberUpsertAfterJobListener memberUpsertAfterJobListener;

    @Bean
    public Job syncRedisToMysqlJob(@Qualifier("stepManager") final Step stepManager,
                                   final JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(stepManager)
                .end()
                .listener(memberUpsertAfterJobListener)
                .build();
    }

    @Bean
    public Step stepManager(@Qualifier("syncRedisToMySqlStep") Step partitionStep, JobRepository jobRepository) {
        return new StepBuilder("stepManager", jobRepository)
                .partitioner("syncRedisToMySqlStep", partitioner())
                .step(partitionStep)
                .gridSize(PARTITION_SIZE) // 파티션 수
                .taskExecutor(executor())
                .build();
    }

    @Bean
    @StepScope
    public Partitioner partitioner() {
        return new RedisRangePartitioner(RedisKeyGenerator.getDailyClickCountKey(), redisTemplate);
    }

    @Bean
    public Step syncRedisToMySqlStep(final ItemReader<TypedTuple<String>> reader,
                                     final ItemWriter<Pair<UpsertMember, DailyClickCount>> writer,
                                     final ItemProcessor<TypedTuple<String>, Pair<UpsertMember, DailyClickCount>> processor,
                                     final JobRepository jobRepository,
                                     final PlatformTransactionManager transactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<TypedTuple<String>, Pair<UpsertMember, DailyClickCount>>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(POOL_SIZE);
        executor.setMaxPoolSize(POOL_SIZE);
        executor.setThreadNamePrefix("partition-thread");
        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        executor.initialize();
        return executor;
    }

    @Bean
    @StepScope
    public ItemStreamReader<TypedTuple<String>> reader(
            @Value("#{stepExecutionContext[startOffset]}") Long startOffset,
            @Value("#{stepExecutionContext[endOffset]}") Long endOffset
    ) {
        final String key = RedisKeyGenerator.getDailyClickCountKey();
        return new RedisPagingItemReader(key, redisTemplate, startOffset, endOffset);
    }

    @Bean
    @StepScope
    public ItemProcessor<TypedTuple<String>, Pair<UpsertMember, DailyClickCount>> processor(
            @Value("#{jobParameters['createAt']}") final String createAt
    ) {
        return tuple -> {
            final String name = tuple.getValue();
            Long clickCount = heartRepository.getClickCount(name);

            return Pair.of(
                    new UpsertMember(name, clickCount),
                    new DailyClickCount(name, LocalDate.parse(createAt), tuple.getScore().longValue())
            );
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Pair<UpsertMember, DailyClickCount>> writer() {
        return new MysqlItemWriter(memberRepository);
    }
}
