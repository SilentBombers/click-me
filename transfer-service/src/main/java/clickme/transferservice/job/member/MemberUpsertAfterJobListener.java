package clickme.transferservice.job.member;

import clickme.transferservice.repository.DailyClickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUpsertAfterJobListener implements JobExecutionListener {

    private static final String DAILY_CLICK_COUNT_KEY = "%s:dailyClickCount";

    private final DailyClickRepository dailyClickRepository;

    @Override
    public void afterJob(final JobExecution jobExecution) {
        final JobParameters jobParameters = jobExecution.getJobParameters();
        final String createAt = jobParameters.getString("createAt");
        dailyClickRepository.deleteKey(DAILY_CLICK_COUNT_KEY.formatted(createAt));
    }
}
