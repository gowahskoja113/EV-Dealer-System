package com.swp391.evdealersystem.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ApiError {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private String method;
    private Map<String, String> details;
    private String traceId;

    public ApiError() {}

    public ApiError(ErrorCode ec, String message) {
        this.timestamp = OffsetDateTime.now();
        this.status = ec.getStatus().value();
        this.error  = ec.getStatus().getReasonPhrase();
        this.code   = ec.getCode();
        this.message = (message == null || message.isBlank()) ? ec.getDefaultMessage() : message;
    }
}
