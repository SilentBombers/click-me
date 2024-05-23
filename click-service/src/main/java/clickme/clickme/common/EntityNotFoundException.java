package clickme.clickme.common;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }
}
