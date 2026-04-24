package com.campustrade.common.rpc;

import java.io.Serializable;
import java.util.Arrays;

/**
 * RPC request envelope.
 */
public class RpcRequest implements Serializable {
    private String requestId;
    private String serviceName;
    private String methodName;
    private Object[] arguments;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "RpcRequest{"
                + "requestId='" + requestId + '\''
                + ", serviceName='" + serviceName + '\''
                + ", methodName='" + methodName + '\''
                + ", arguments=" + Arrays.toString(arguments)
                + '}';
    }
}
