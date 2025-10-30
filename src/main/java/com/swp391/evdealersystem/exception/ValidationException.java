package com.swp391.evdealersystem.exception;

public class ValidationException extends BaseException {
    public ValidationException() { super(ErrorCode.VALIDATION_FAILED); }
    public ValidationException(String message) { super(ErrorCode.VALIDATION_FAILED, message); }
}
