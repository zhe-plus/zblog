package com.zblog.exception;

import com.zblog.enums.ErrorCode;

public class ResourceNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public ResourceNotFoundException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }

    public ResourceNotFoundException(ErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public ErrorCode getErrorCode() { return errorCode; }
}
