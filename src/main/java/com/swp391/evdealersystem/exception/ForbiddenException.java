package com.swp391.evdealersystem.exception;

public class ForbiddenException extends BaseException {
    public ForbiddenException() { super(ErrorCode.FORBIDDEN); }
    public ForbiddenException(String message) { super(ErrorCode.FORBIDDEN, message); }
}
