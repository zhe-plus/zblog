package com.zblog.exception;

import com.zblog.enums.ErrorCode;

public class BadRequestException extends RuntimeException {
    private final ErrorCode errorCode;

    public BadRequestException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }

    public BadRequestException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public ErrorCode getErrorCode() { return errorCode; }
}
