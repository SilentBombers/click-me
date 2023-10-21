package clickme.transferservice.job.member;

import clickme.transferservice.domain.Member;
import clickme.transferservice.repository.MemberRepository;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MemberUpsertJobConfig {

    private static final int CHUCK_SIZE = 10;
    private static final String REDIS_KEY = "clicks";
    private static final String STEP_NAME = "syncRedisToMysqlStep";
    private static final String JOB_NAME = "syncRedisToMysqlJob";


    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;

    public MemberUpsertJobConfig(final RedisTemplate<String, String> redisTemplate, final MemberRepository memberRepository) {
        this.redisTemplate = redisTemplate;
        this.memberRepository = memberRepository;
    }

    @Bean
    @StepScope
    public ItemStreamReader<TypedTuple<String>> reader() {
        return new RedisCursorItemReader(REDIS_KEY, redisTemplate);
    }

    @Bean
    @StepScope
    public ItemProcessor<TypedTuple<String>, Member> processor() {
        return tuple -> {
            String nickname = tuple.getValue();
            Long clickCount = tuple.getScore()
                    .longValue();

            return new Member(nickname, clickCount);
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Member> writer() {
        return new MysqlItemWriter(memberRepository);
    }

    @Bean
    @JobScope
    public Step syncRedisToMySqlStep(final ItemReader<TypedTuple<String>> reader,
                                     final ItemWriter<Member> writer,
                                     final JobRepository jobRepository,
                                     final PlatformTransactionManager transactionManager) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<TypedTuple<String>, Member>chunk(CHUCK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job syncRedisToMysqlJob(final Step syncRedisToMysqlStep, final JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(syncRedisToMysqlStep)
                .end()
                .build();
    }
}