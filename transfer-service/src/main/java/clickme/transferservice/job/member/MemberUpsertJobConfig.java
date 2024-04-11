package clickme.transferservice.job.member;

import clickme.transferservice.job.member.dto.DailyClickCount;
import clickme.transferservice.job.member.dto.UpsertMember;
import clickme.transferservice.repository.HeartRepository;
import clickme.transferservice.repository.MemberRepository;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.batch.job.name", havingValue="syncRedisToMysqlJob")
public class MemberUpsertJobConfig  {

    private static final int CHUCK_SIZE = 1000;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String REDIS_KEY = "%s:dailyClickCount";
    private static final String STEP_NAME = "syncRedisToMysqlStep";
    private static final String JOB_NAME = "syncRedisToMysqlJob";

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;
    private final MemberUpsertAfterJobListener memberUpsertAfterJobListener;

    @Bean
    public TaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
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
        final String key = REDIS_KEY.formatted(LocalDateTime.now().format(formatter));
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

    @Bean
    public Step syncRedisToMySqlStep(final ItemReader<TypedTuple<String>> reader,
                                     final ItemWriter<Pair<UpsertMember, DailyClickCount>> writer,
                                     final ItemProcessor<TypedTuple<String>, Pair<UpsertMember, DailyClickCount>> processor,
                                     final JobRepository jobRepository,
                                     final PlatformTransactionManager transactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<TypedTuple<String>, Pair<UpsertMember, DailyClickCount>>chunk(CHUCK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step masterStep(@Qualifier("syncRedisToMySqlStep") Step partitionStep,
                           JobRepository jobRepository,
                           PlatformTransactionManager transactionManager) {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner("syncRedisToMySqlStep", partitioner())
                .step(partitionStep)
                .gridSize(10) // 파티션 수
                .taskExecutor(executor())
                .build();
    }

    @Bean
    @StepScope
    public Partitioner partitioner() {
        return new RedisRangePartitioner(REDIS_KEY.formatted(LocalDateTime.now().format(formatter)), 10, redisTemplate);
    }


    @Bean
    public Job syncRedisToMysqlJob(@Qualifier("masterStep") final Step masterStep,
                                   final JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(masterStep)
                .end()
                .listener(memberUpsertAfterJobListener)
                .build();
    }
}
