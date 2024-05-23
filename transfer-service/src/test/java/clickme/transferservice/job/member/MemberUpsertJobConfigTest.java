package clickme.transferservice.job.member;

import clickme.transferservice.TestBatchConfig;
import clickme.transferservice.util.RedisKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringJUnitConfig(classes = {MemberUpsertJobConfig.class, TestBatchConfig.class, MemberUpsertAfterJobListener.class})
@TestPropertySource(properties = {"spring.batch.job.name = syncRedisToMysqlJob", "SLACK_WEBHOOK_URL=https://example.com"})
class MemberUpsertJobConfigTest extends AbstractIntegrationTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final JobParameters JOB_PARAMETERS = new JobParametersBuilder()
            .addString("createAt", LocalDateTime.now().format(formatter))
            .toJobParameters();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> dailyClickCounts;

    @BeforeEach
    void setUp() {
        redisTemplate.delete(RedisKeyGenerator.getDailyClickCountKey());
        dailyClickCounts = redisTemplate.opsForZSet();

        jdbcTemplate.execute("DELETE FROM click_count_history");
        jdbcTemplate.execute("DELETE FROM member");
    }

    @Test
    void 일일_클릭카운트가_정상적으로_db에_저장된다() throws Exception {
        일일_클릭카운트를_추가한다("a", 2L);
        일일_클릭카운트를_추가한다("b", 5L);

        final JobExecution jobExecution = jobLauncherTestUtils.launchJob(JOB_PARAMETERS);
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM click_count_history", Integer.class);

        assertAll(
                () -> assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED"),
                () -> assertThat(count).isEqualTo(2)
        );
    }

    @Test
    void 당일_클릭카운트가_존재해야_정상적으로_member테이블에_반영된다() throws Exception {
        일일_클릭카운트를_추가한다("seungpang", 1L);

        final JobExecution jobExecution = jobLauncherTestUtils.launchJob(JOB_PARAMETERS);
        final int count = jdbcTemplate.queryForObject("SELECT click_count FROM member WHERE name = ?", new Object[]{"seungpang"}, Integer.class);
        System.out.println(count);
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    }

    @Test
    void 당일_클릭카운가_존재하지_않으면_member테이블에_반영되지_않는다() throws Exception {
        final JobExecution jobExecution = jobLauncherTestUtils.launchJob(JOB_PARAMETERS);
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM member", Integer.class);

        assertAll(
                () -> assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED"),
                () -> assertThat(count).isZero()
        );
    }

    private void 일일_클릭카운트를_추가한다(String name, Long clickCount) {
        dailyClickCounts.add(RedisKeyGenerator.getDailyClickCountKey(), name, clickCount);
    }
}
