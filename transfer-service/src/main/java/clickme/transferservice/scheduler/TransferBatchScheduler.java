package clickme.transferservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TransferBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job syncRedisToMysqlJob;
    private final Job profileImageUpdateJob;

    @Scheduled(cron = "0 0 0 * * *")
    public void runSyncRedisToMysqlJob() throws Exception {
        final JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(syncRedisToMysqlJob, jobParameters);
    }

    @Scheduled(cron = "0 30 0 * * *")
    public void runUpdateGithubProfileImageJob() throws Exception {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final JobParameters jobParameters = new JobParametersBuilder()
                .addString("createAt", LocalDateTime.now().format(formatter))
                .toJobParameters();

        jobLauncher.run(profileImageUpdateJob, jobParameters);
    }
}
