package clickme.clickme.upload.application.exception;

import clickme.clickme.common.BusinessException;
import clickme.clickme.common.ErrorCode;

public class SvgConvertException extends BusinessException {

    public SvgConvertException() {
        super(ErrorCode.SVG_CONVERSION_FAILED);
    }
}
