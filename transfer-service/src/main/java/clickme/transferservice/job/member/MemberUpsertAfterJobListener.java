package clickme.transferservice.job.member;

import clickme.transferservice.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@RequiredArgsConstructor
public class MemberUpsertAfterJobListener implements JobExecutionListener {

    private static final String KEY = "clickCountChanged";

    private final HeartRepository heartRepository;

    @Override
    public void afterJob(final JobExecution jobExecution) {
        heartRepository.deleteKey(KEY);
    }
}
