package clickme.transferservice.job.member;

import clickme.transferservice.domain.ProfileUpdateMember;
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

    private final MemberItemProcessor memberItemProcessor;
    private final MemberRepository memberRepository;
    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<NicknameMember> jdbcPagingItemReader(final DataSource dataSource,
                                                             @Value("#{jobParameters['createAt']}") String createAt) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createAt", createAt);

        return new JdbcPagingItemReaderBuilder<NicknameMember>()
                .name("memberItemReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider(dataSource))
                .parameterValues(parameters)
                .pageSize(1000)
                .rowMapper(new BeanPropertyRowMapper<>(NicknameMember.class))
                .build();
    }

    private PagingQueryProvider queryProvider(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("SELECT id, nickname");
        factoryBean.setFromClause("FROM member");
        factoryBean.setWhereClause("WHERE DATE_FORMAT(create_at, '%Y-%m-%d') = :createAt");
        factoryBean.setSortKey("id");

        try {
            return factoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException("해당 쿼리의 결과가 올바르지 않습니다.");
        }
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
                                       final ItemWriter<ProfileUpdateMember> writer) {
        return new StepBuilder("profileImageUpdateStep", jobRepository)
                .<NicknameMember, ProfileUpdateMember>chunk(1000, transactionManager)
                .reader(reader)
                .processor(memberItemProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job profileImageUpdateJob(final Step profileImageUpdateStep, final JobRepository jobRepository) {
        return new JobBuilder("profileImageUpdateJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(profileImageUpdateStep)
                .end()
                .build();
    }
}
