package clickme.transferservice.service.exception;

public class GithubApiException extends RuntimeException {

    public GithubApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
