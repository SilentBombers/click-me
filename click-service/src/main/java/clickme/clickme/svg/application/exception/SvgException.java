package clickme.clickme.svg.application.exception;

import clickme.clickme.common.BusinessException;
import clickme.clickme.common.ErrorCode;

public class SvgException extends BusinessException {

    public SvgException(final String message, final ErrorCode errorCode) {
        super(message, errorCode);
    }
}
