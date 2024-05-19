package clickme.transferservice.job.member.exception;

public class SlackWebhookException extends RuntimeException {

    public SlackWebhookException() {
        super("slack webhook 송신중에 문제가 발생했습니다.");
    }
}
