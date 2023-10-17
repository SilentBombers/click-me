package clickme.transferservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job syncRedisToMysqlJob;

    @Scheduled(fixedRate = 60000)
    public void runSyncRedisToMysqlJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(syncRedisToMysqlJob, jobParameters);
    }
}
