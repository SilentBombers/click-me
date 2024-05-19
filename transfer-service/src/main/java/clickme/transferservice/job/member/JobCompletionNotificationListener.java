package clickme.transferservice.job.member;

import clickme.transferservice.job.member.exception.SlackWebhookException;
import clickme.transferservice.support.HttpClientSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final HttpClientSupport httpClientSupport;
    private final String slackWebhookURL;

    public JobCompletionNotificationListener(
            final HttpClientSupport httpClientSupport,
            @Value("${SLACK_WEBHOOK_URL}") final String slackWebhookURL
    ) {
        this.httpClientSupport = httpClientSupport;
        this.slackWebhookURL = slackWebhookURL;
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            sendMessageToSlack("Job 성공 알림: " + jobExecution.getJobInstance().getJobName() + "이 성공하였습니다.");
        }

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            sendMessageToSlack("Job 실패 알림: " + jobExecution.getJobInstance().getJobName() + "이 실패하였습니다.");
        }
    }

    private void sendMessageToSlack(final String message) {
        final String payload = "{\"text\": \"" + message + "\"}";

        try {
            httpClientSupport.sendPostRequestWithBody(slackWebhookURL, payload);
            log.info("메시지가 정상적으로 전송되었습니다.");
        } catch (Exception e) {
            log.error("메시지가 정상적으로 전송되지 못했습니다. ", e);
            throw new SlackWebhookException();
        }
    }
}
