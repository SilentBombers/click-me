package clickme.clickme.repository.exception;

public class NotFoundMemberException extends RuntimeException {

    public NotFoundMemberException(final String name) {
        super("존재하지 않는 사용자입니다. name = %s".formatted(name));
    }
}
