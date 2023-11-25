package clickme.transferservice.job.member;

import clickme.transferservice.job.member.dto.DailyClickCount;
import clickme.transferservice.job.member.dto.UpsertMember;
import clickme.transferservice.repository.DailyClickRepository;
import clickme.transferservice.repository.HeartRepository;
import clickme.transferservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@RequiredArgsConstructor
public class MemberUpsertJobConfig  {

    private static final int CHUCK_SIZE = 1000;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String REDIS_KEY = "%s:dailyClickCount";
    private static final String STEP_NAME = "syncRedisToMysqlStep";
    private static final String JOB_NAME = "syncRedisToMysqlJob";

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final DailyClickRepository dailyClickRepository;
    private final HeartRepository heartRepository;
    private final MemberUpsertAfterJobListener memberUpsertAfterJobListener;

    @Bean
    @StepScope
    public ItemStreamReader<String> reader() {
        final String key = REDIS_KEY.formatted(LocalDateTime.now().format(formatter));
        return new RedisPagingItemReader(key, redisTemplate);
    }

    @Bean
    @StepScope
    public ItemProcessor<String, Pair<UpsertMember, DailyClickCount>> processor(
            @Value("#{jobParameters['createAt']}") final String createAt
    ) {
        return name -> {
            Long clickCount = heartRepository.getClickCount(name);
            Long dailyClickCount = dailyClickRepository.getClickCount(name);
            return Pair.of(
                    new UpsertMember(name, clickCount),
                    new DailyClickCount(name, LocalDate.parse(createAt), dailyClickCount)
            );
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Pair<UpsertMember, DailyClickCount>> writer() {
        return new MysqlItemWriter(memberRepository);
    }

    @Bean
    @JobScope
    public Step syncRedisToMySqlStep(final ItemReader<String> reader,
                                     final ItemWriter<Pair<UpsertMember, DailyClickCount>> writer,
                                     final ItemProcessor<String, Pair<UpsertMember, DailyClickCount>> processor,
                                     final JobRepository jobRepository,
                                     final PlatformTransactionManager transactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<String, Pair<UpsertMember, DailyClickCount>>chunk(CHUCK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
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
