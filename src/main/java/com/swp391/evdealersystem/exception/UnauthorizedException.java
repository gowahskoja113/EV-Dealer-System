package com.swp391.evdealersystem.exception;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException() { super(ErrorCode.UNAUTHORIZED); }
    public UnauthorizedException(String message) { super(ErrorCode.UNAUTHORIZED, message); }
}
