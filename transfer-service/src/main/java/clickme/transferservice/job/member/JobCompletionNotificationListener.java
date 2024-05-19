package clickme.transferservice.job.member;

import clickme.transferservice.job.member.exception.SlackWebhookException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Value("${slack.webhook.url}")
    private String slackWebhookURL;

    private final ApplicationContext context;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final HttpClient httpClient;

    public JobCompletionNotificationListener(
            ApplicationContext context,
            @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor,
            HttpClient httpClient
    ) {
        this.context = context;
        this.taskExecutor = taskExecutor;
        this.httpClient = httpClient;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED)  {
            sendMessageToSlack("Job 성공 알림: " + jobExecution.getJobInstance().getJobName() + "이 성공하였습니다.");
        }

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            sendMessageToSlack("Job 실패 알림: " + jobExecution.getJobInstance().getJobName() + "이 실패하였습니다.");
        }
        taskExecutor.shutdown();
    }

    private void sendMessageToSlack(String message) {
        String payload = "{\"text\": \"" + message + "\"}";

        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(slackWebhookURL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                log.info("메시지가 정상적으로 전송되었습니다.");
            } else {
                log.error("메시지가 정상적으로 전송되지 못했습니다. 상태코드 = {}", response.statusCode());
            }
        } catch (Exception e) {
            throw new SlackWebhookException();
        }
    }
}
