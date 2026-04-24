package com.campustrade.gateway.exception;

import com.campustrade.common.result.Result;
import com.campustrade.common.result.ResultCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException ex) {
        return Result.failure(ex.getResultCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = "Validation failed";
        if (ex.getBindingResult().hasErrors()) {
            FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
            message = fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }
        return Result.failure(ResultCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException ex) {
        return Result.failure(ResultCode.VALIDATION_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return Result.failure(ResultCode.BAD_REQUEST, "Invalid parameter: " + ex.getName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return Result.failure(ResultCode.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RemoteServiceException.class)
    public Result<Void> handleRemoteService(RemoteServiceException ex) {
        return Result.failure(ResultCode.REMOTE_SERVICE_ERROR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        return Result.failure(ResultCode.INTERNAL_ERROR, ex.getMessage());
    }
}
