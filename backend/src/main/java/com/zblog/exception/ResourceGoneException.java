package com.zblog.exception;

import com.zblog.enums.ErrorCode;

public class ResourceGoneException extends RuntimeException {
    private final ErrorCode errorCode;

    public ResourceGoneException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }

    public ResourceGoneException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public ErrorCode getErrorCode() { return errorCode; }
}
