package com.zblog.dto.response;

import com.zblog.enums.ErrorCode;

public class ErrorResponse {
    private ErrorDetail error;

    public ErrorResponse() {}

    public ErrorResponse(ErrorCode code) {
        this.error = new ErrorDetail(code.getCode(), code.getMessage());
    }

    public ErrorResponse(ErrorCode code, String message) {
        this.error = new ErrorDetail(code.getCode(), message);
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(ErrorCode code, String message) {
        return new ErrorResponse(code, message);
    }

    public ErrorDetail getError() { return error; }
    public void setError(ErrorDetail error) { this.error = error; }

    public static class ErrorDetail {
        private String code;
        private String message;

        public ErrorDetail() {}
        public ErrorDetail(String code, String message) { this.code = code; this.message = message; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
