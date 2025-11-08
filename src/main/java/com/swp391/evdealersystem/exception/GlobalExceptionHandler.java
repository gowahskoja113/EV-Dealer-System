package com.swp391.evdealersystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> build(HttpServletRequest req, ErrorCode code, String message, Map<String,String> details) {
        ApiError body = new ApiError(code, message);
        body.setPath(req != null ? req.getRequestURI() : null);
        body.setMethod(req != null ? req.getMethod() : null);
        body.setDetails(details);
        return ResponseEntity.status(code.getStatus()).body(body);
    }
    private ResponseEntity<Object> build(HttpServletRequest req, ErrorCode code, String message) {
        return build(req, code, message, null);
    }
    private HttpServletRequest req(WebRequest webRequest) {
        if (webRequest instanceof NativeWebRequest nwr) {
            return nwr.getNativeRequest(HttpServletRequest.class);
        }
        return null;
    }

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<Object> handleBase(HttpServletRequest req, BaseException ex) {
        return build(req, ex.getErrorCode(), ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest webRequest) {

        Map<String, String> details = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.put(fe.getField(), fe.getDefaultMessage());
        }
        return build(req(webRequest), ErrorCode.VALIDATION_FAILED,
                ErrorCode.VALIDATION_FAILED.getDefaultMessage(), details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(HttpServletRequest req, ConstraintViolationException ex) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v ->
                details.put(v.getPropertyPath().toString(), v.getMessage())
        );
        return build(req, ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getDefaultMessage(), details);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest webRequest) {
        return build(req(webRequest), ErrorCode.BAD_REQUEST, "JSON/body không hợp lệ");
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest webRequest) {
        Map<String,String> details = Map.of(ex.getParameterName(), "Thiếu tham số bắt buộc");
        return build(req(webRequest), ErrorCode.BAD_REQUEST, "Thiếu tham số", details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleTypeMismatch(HttpServletRequest req, MethodArgumentTypeMismatchException ex) {
        Map<String,String> details = Map.of(String.valueOf(ex.getName()), "Sai kiểu dữ liệu");
        return build(req, ErrorCode.BAD_REQUEST, "Kiểu dữ liệu không hợp lệ", details);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest webRequest) {
        return build(req(webRequest), ErrorCode.NOT_FOUND, "Endpoint không tồn tại");
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest webRequest) {
        return build(req(webRequest), ErrorCode.METHOD_NOT_ALLOWED, "HTTP method không hỗ trợ");
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest webRequest) {
        return build(req(webRequest), ErrorCode.UNSUPPORTED_MEDIA_TYPE, "Content-Type không hỗ trợ");
    }

    // ===== Security =====
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuth(HttpServletRequest req, AuthenticationException ex) {
        return build(req, ErrorCode.UNAUTHORIZED, "Bạn cần đăng nhập");
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(HttpServletRequest req, AccessDeniedException ex) {
        return build(req, ErrorCode.FORBIDDEN, "Bạn không có quyền thực hiện");
    }

    // ===== Data layer =====
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrity(HttpServletRequest req, DataIntegrityViolationException ex) {
        return build(req, ErrorCode.CONFLICT, "Vi phạm ràng buộc dữ liệu / xung đột");
    }

    // ===== Fallback =====
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAll(HttpServletRequest req, Exception ex) {
        return build(req, ErrorCode.INTERNAL_ERROR, "Có lỗi không mong muốn, vui lòng thử lại");
    }
}
