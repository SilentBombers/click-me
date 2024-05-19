package clickme.transferservice.job.member;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final ThreadPoolTaskExecutor taskExecutor;

    public JobCompletionNotificationListener(
            @Qualifier("taskExecutor")ThreadPoolTaskExecutor taskExecutor
    ) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED || jobExecution.getStatus() == BatchStatus.FAILED) {
            taskExecutor.shutdown();
        }
    }
}
