package clickme.transferservice.service.exception;

public class GithubApiException extends RuntimeException {

    public GithubApiException() {
        super("github api 호출중에 문제가 발생했습니다.");
    }
}
