package com.campustrade.userservice.exception;

import com.campustrade.common.result.ResultCode;

public class BusinessException extends RuntimeException {
    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
