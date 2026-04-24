package com.campustrade.common.result;

/**
 * Unified result code definition for all services.
 */
public enum ResultCode {
    SUCCESS(0, "Success"),
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    CONFLICT(409, "Conflict"),
    TOO_MANY_REQUESTS(429, "Too many requests"),
    VALIDATION_ERROR(1001, "Validation failed"),
    REMOTE_SERVICE_ERROR(1002, "Remote service error"),
    INTERNAL_ERROR(5000, "Internal server error");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
