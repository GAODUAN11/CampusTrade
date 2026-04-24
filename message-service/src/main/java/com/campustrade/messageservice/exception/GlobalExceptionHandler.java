package com.campustrade.messageservice.exception;

import com.campustrade.common.result.Result;
import com.campustrade.common.result.ResultCode;
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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return Result.failure(ResultCode.BAD_REQUEST, "Invalid parameter: " + ex.getName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = "Validation failed";
        if (ex.getBindingResult().hasErrors()) {
            FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
            message = fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }
        return Result.failure(ResultCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        return Result.failure(ResultCode.INTERNAL_ERROR, ex.getMessage());
    }
}
