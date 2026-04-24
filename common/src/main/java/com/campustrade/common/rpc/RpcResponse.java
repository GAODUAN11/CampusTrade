package com.campustrade.common.rpc;

import java.io.Serializable;

/**
 * RPC response envelope.
 */
public class RpcResponse implements Serializable {
    private String requestId;
    private boolean success;
    private Object data;
    private Integer errorCode;
    private String errorMessage;

    public static RpcResponse success(String requestId, Object data) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static RpcResponse failure(String requestId, Integer errorCode, String errorMessage) {
        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
