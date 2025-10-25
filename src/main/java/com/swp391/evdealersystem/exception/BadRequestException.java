package com.swp391.evdealersystem.exception;

public class BadRequestException extends BaseException {
    public BadRequestException() { super(ErrorCode.BAD_REQUEST); }
    public BadRequestException(String message) { super(ErrorCode.BAD_REQUEST, message); }
}
