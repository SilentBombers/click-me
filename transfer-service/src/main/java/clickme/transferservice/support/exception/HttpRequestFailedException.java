package clickme.transferservice.support.exception;

public class HttpRequestFailedException extends RuntimeException {

    public HttpRequestFailedException(int statusCode) {
        super("http 요청이 실패했습니다. 상태코드 = %d".formatted(statusCode));
    }
}
