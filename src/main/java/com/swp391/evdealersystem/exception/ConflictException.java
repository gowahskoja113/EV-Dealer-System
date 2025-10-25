package com.swp391.evdealersystem.exception;

public class ConflictException extends BaseException {
    public ConflictException() { super(ErrorCode.CONFLICT); }
    public ConflictException(String message) { super(ErrorCode.CONFLICT, message); }
}
