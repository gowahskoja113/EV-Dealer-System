package com.swp391.evdealersystem.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 4xx
    VALIDATION_FAILED("VALIDATION_FAILED", HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "Chưa xác thực"),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN, "Không có quyền truy cập"),
    NOT_FOUND("NOT_FOUND", HttpStatus.NOT_FOUND, "Không tìm thấy tài nguyên"),
    CONFLICT("CONFLICT", HttpStatus.CONFLICT, "Dữ liệu xung đột"),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED, "Phương thức không hỗ trợ"),
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Media type không hỗ trợ"),

    // 5xx
    DATABASE_ERROR("DATABASE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi cơ sở dữ liệu"),
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống");

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
    public String getCode() { return code; }
    public HttpStatus getStatus() { return status; }
    public String getDefaultMessage() { return defaultMessage; }
}
