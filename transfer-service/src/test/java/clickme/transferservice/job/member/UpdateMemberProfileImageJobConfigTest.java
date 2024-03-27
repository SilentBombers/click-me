package clickme.transferservice.job.member;

import clickme.transferservice.TestBatchConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.Date;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringJUnitConfig(classes = {UpdateMemberProfileImageJobConfig.class, TestBatchConfig.class})
@TestPropertySource(properties = {"spring.batch.job.name = profileImageUpdateJob"})
class UpdateMemberProfileImageJobConfigTest extends AbstractIntegrationTest {

    private static final String DEFAULT_IMAGE = "https://avatars.githubusercontent.com/u/134919246?v=4";
    private static final String SEUNGPANG_IMAGE = "https://avatars.githubusercontent.com/u/37570657?v=4";
    private static final JobParameters JOB_PARAMETERS = new JobParametersBuilder()
            .addString("createAt", LocalDateTime.now().format(formatter))
            .toJobParameters();

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void db에_신규사용자가_들어올경우_프로필_사진_업데이트가_정상적으로_동작한다() throws Exception {
        jdbcTemplate.update("INSERT INTO member(click_count, name, created_at) VALUES (?, ?, ?)",
                0, "seungpang", Date.valueOf(LocalDateTime.now().format(formatter)));

        final JobExecution jobExecution = jobLauncherTestUtils.launchJob(JOB_PARAMETERS);

        final String imageUrl = jdbcTemplate.queryForObject(
                "SELECT profile_image_url FROM member WHERE name = ?", new Object[]{"seungpang"}, String.class
        );

        assertAll(
                () -> assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED"),
                () -> assertThat(imageUrl).isEqualTo(SEUNGPANG_IMAGE)
        );
    }

    @Test
    void 신규_사용자의_이미지를_가져오지_못할_경우_기본이미지로_대체된다() throws Exception {
        jdbcTemplate.update("INSERT INTO member(click_count, name, created_at) VALUES (?, ?, ?)",
                0, "불가능한아이디", Date.valueOf(LocalDateTime.now().format(formatter)));

        final JobExecution jobExecution = jobLauncherTestUtils.launchJob(JOB_PARAMETERS);
        final String imageUrl = jdbcTemplate.queryForObject(
                "SELECT profile_image_url FROM member WHERE name = ?", new Object[]{"불가능한아이디"}, String.class
        );

        assertAll(
                () -> assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED"),
                () -> assertThat(imageUrl).isEqualTo(DEFAULT_IMAGE)
        );
    }

    @Test
    void 오늘보다_이전의_유저의_프로필은_업데이트하지_않는다() throws Exception {
        jdbcTemplate.update("INSERT INTO member(click_count, name, created_at) VALUES (?, ?, ?)",
                0, "seungpang", Date.valueOf(LocalDateTime.of(2023, 12, 10, 10, 10).format(formatter)));

        final JobExecution jobExecution = jobLauncherTestUtils.launchJob(JOB_PARAMETERS);
        final String imageUrl = jdbcTemplate.queryForObject(
                "SELECT profile_image_url FROM member WHERE name = ?", new Object[]{"seungpang"}, String.class
        );

        assertAll(
                () -> assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED"),
                () -> assertThat(imageUrl).isNull()
        );
    }
}
