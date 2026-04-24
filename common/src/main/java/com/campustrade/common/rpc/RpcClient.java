package com.campustrade.common.rpc;

import com.campustrade.common.rpc.protocol.JavaRpcSerializer;
import com.campustrade.common.rpc.protocol.RpcFrameCodec;
import com.campustrade.common.rpc.protocol.RpcProtocol;
import com.campustrade.common.rpc.protocol.RpcProtocolException;
import com.campustrade.common.rpc.protocol.RpcSerializer;
import com.campustrade.common.rpc.protocol.RpcSerializerRegistry;
import com.campustrade.common.rpc.protocol.RpcTransportPacket;
import com.campustrade.common.result.ResultCode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.UUID;

/**
 * Lightweight socket RPC client.
 */
public class RpcClient {
    private final String host;
    private final int port;
    private final int timeoutMillis;
    private final byte protocolVersion;
    private final RpcSerializer serializer;

    public RpcClient(String host, int port, Duration timeout) {
        this(host, port, timeout, RpcProtocol.CURRENT_VERSION, JavaRpcSerializer.NAME);
    }

    public RpcClient(String host, int port, Duration timeout, Byte protocolVersion, String serializerName) {
        this.host = host;
        this.port = port;
        this.timeoutMillis = (int) Math.max(1000L, timeout.toMillis());
        this.protocolVersion = protocolVersion == null ? RpcProtocol.CURRENT_VERSION : protocolVersion;
        try {
            this.serializer = RpcSerializerRegistry.requireByName(
                    serializerName == null || serializerName.isBlank() ? JavaRpcSerializer.NAME : serializerName
            );
        } catch (RpcProtocolException ex) {
            throw new IllegalArgumentException("Invalid RPC serializer config: " + serializerName, ex);
        }
    }

    public RpcClient(String host, int port, Duration timeout, byte protocolVersion, RpcSerializer serializer) {
        this.host = host;
        this.port = port;
        this.timeoutMillis = (int) Math.max(1000L, timeout.toMillis());
        this.protocolVersion = protocolVersion;
        this.serializer = serializer;
    }

    @SuppressWarnings("unchecked")
    public <T> T invoke(String serviceName, String methodName, Object... arguments) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setServiceName(serviceName);
        request.setMethodName(methodName);
        request.setArguments(arguments);

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMillis);
            socket.setSoTimeout(timeoutMillis);

            try (BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
                 BufferedInputStream input = new BufferedInputStream(socket.getInputStream())) {
                RpcFrameCodec.writeRequest(output, request, protocolVersion, serializer);
                RpcTransportPacket<RpcResponse> packet = RpcFrameCodec.readResponse(input);
                RpcResponse response = packet.getPayload();
                if (!response.isSuccess()) {
                    Integer code = response.getErrorCode();
                    String message = response.getErrorMessage();
                    throw new RpcException(
                            code == null ? ResultCode.REMOTE_SERVICE_ERROR.getCode() : code,
                            message == null ? "RPC invocation failed." : message
                    );
                }
                return (T) response.getData();
            }
        } catch (RpcException ex) {
            throw ex;
        } catch (RpcProtocolException ex) {
            throw new RpcException(ResultCode.REMOTE_SERVICE_ERROR.getCode(), ex.getMessage());
        } catch (Exception ex) {
            throw new RpcException(
                    "Failed to invoke RPC " + serviceName + "." + methodName
                            + " by " + host + ":" + port, ex
            );
        }
    }
}
