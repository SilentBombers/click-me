package clickme.transferservice.job.member;

import clickme.transferservice.job.member.dto.ProfileUpdateMember;
import clickme.transferservice.repository.MemberRepository;
import clickme.transferservice.service.GithubApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class UpdateMemberProfileImageJobConfig {

    private static final String JOB_NAME = "profileImageUpdateJob";
    private static final String STEP_NAME = "profileImageUpdateStep";
    private static final int CHUCK_SIZE = 1000;
    private static final int PAGE_SIZE = 1000;

    private final MemberRepository memberRepository;
    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<NicknameMember> jdbcPagingItemReader(
            final DataSource dataSource,
            @Value("#{jobParameters['createAt']}") final String createAt
    ) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createAt", createAt);

        return new JdbcPagingItemReaderBuilder<NicknameMember>()
                .name("memberItemReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider())
                .parameterValues(parameters)
                .pageSize(PAGE_SIZE)
                .rowMapper(new BeanPropertyRowMapper<>(NicknameMember.class))
                .build();
    }

    @Bean
    public PagingQueryProvider queryProvider() {
        try {
            return new SqlPagingQueryProviderFactoryBean() {
                {
                    setDataSource(dataSource);
                    setSelectClause("SELECT id, name");
                    setFromClause("FROM member");
                    setWhereClause("WHERE DATE_FORMAT(created_at, '%Y-%m-%d') = :createAt");
                    setSortKey("id");
                }
            }.getObject();
        } catch (Exception e) {
            throw new RuntimeException("쿼리 프로바이더 생성 중 오류가 발생했습니다.", e);
        }
    }

    @Bean
    @StepScope
    public ItemProcessor<NicknameMember, ProfileUpdateMember> memberItemProcessor(final GithubApiService githubApiService) {
        return new MemberItemProcessor(githubApiService);
    }

    @Bean
    @StepScope
    public ItemWriter<ProfileUpdateMember> profileUpdateMemberItemWriter() {
        return new MysqlProfileWriter(memberRepository);
    }

    @Bean
    @JobScope
    public Step profileImageUpdateStep(final JobRepository jobRepository,
                                       final PlatformTransactionManager transactionManager,
                                       final JdbcPagingItemReader<NicknameMember> reader,
                                       final ItemProcessor<NicknameMember, ProfileUpdateMember> processor,
                                       final ItemWriter<ProfileUpdateMember> writer) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .<NicknameMember, ProfileUpdateMember>chunk(CHUCK_SIZE, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job profileImageUpdateJob(final Step profileImageUpdateStep, final JobRepository jobRepository) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(profileImageUpdateStep)
                .end()
                .build();
    }
}
