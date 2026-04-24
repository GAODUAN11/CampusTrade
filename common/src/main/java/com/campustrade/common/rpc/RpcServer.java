package com.campustrade.common.rpc;

import com.campustrade.common.rpc.protocol.RpcFrameCodec;
import com.campustrade.common.rpc.protocol.RpcProtocol;
import com.campustrade.common.rpc.protocol.RpcProtocolException;
import com.campustrade.common.rpc.protocol.RpcTransportPacket;
import com.campustrade.common.result.ResultCode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Lightweight socket RPC server.
 */
public class RpcServer {
    private final int port;
    private final Map<String, RpcServiceHandler> handlerMap;
    private final ExecutorService workerPool;
    private final byte supportedProtocolVersion;
    private volatile boolean running;
    private ServerSocket serverSocket;
    private Thread acceptThread;

    public RpcServer(int port, Map<String, RpcServiceHandler> handlerMap, int workerThreads) {
        this(port, handlerMap, workerThreads, RpcProtocol.CURRENT_VERSION);
    }

    public RpcServer(int port, Map<String, RpcServiceHandler> handlerMap, int workerThreads, byte supportedProtocolVersion) {
        this.port = port;
        this.handlerMap = handlerMap;
        this.workerPool = Executors.newFixedThreadPool(Math.max(workerThreads, 4));
        this.supportedProtocolVersion = supportedProtocolVersion;
    }

    public synchronized void start() {
        if (running) {
            return;
        }
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to start RPC server on port " + port, ex);
        }

        running = true;
        this.acceptThread = new Thread(this::acceptLoop, "campustrade-rpc-" + port);
        this.acceptThread.setDaemon(true);
        this.acceptThread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (Exception ignored) {
        }
        workerPool.shutdownNow();
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                workerPool.execute(() -> handleSocket(socket));
            } catch (SocketException socketClosed) {
                if (!running) {
                    return;
                }
            } catch (Exception ignored) {
                // Keep server alive even if one accept failed.
            }
        }
    }

    private void handleSocket(Socket socket) {
        try (Socket ignoredSocket = socket;
             BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream())) {

            RpcTransportPacket<RpcRequest> packet = RpcFrameCodec.readRequest(input);
            RpcRequest request = packet.getPayload();

            RpcResponse response = dispatch(request);
            byte version = resolveResponseVersion(packet.getProtocolVersion());
            RpcFrameCodec.writeResponse(output, response, version, packet.getSerializer());
        } catch (RpcProtocolException ignored) {
            // For malformed frames, close socket directly.
        } catch (Exception ignored) {
            // Connection level failure can be ignored for this lightweight server.
        }
    }

    private RpcResponse dispatch(RpcRequest request) {
        RpcServiceHandler handler = handlerMap.get(request.getServiceName());
        if (handler == null) {
            return RpcResponse.failure(
                    request.getRequestId(),
                    ResultCode.NOT_FOUND.getCode(),
                    "RPC service not found: " + request.getServiceName()
            );
        }

        try {
            Object[] args = request.getArguments() == null ? new Object[0] : request.getArguments();
            Object result = handler.handle(request.getMethodName(), args);
            return RpcResponse.success(request.getRequestId(), result);
        } catch (Throwable ex) {
            Throwable root = unwrap(ex);
            ResultCode code = resolveResultCode(root);
            String message = root.getMessage();
            return RpcResponse.failure(
                    request.getRequestId(),
                    code.getCode(),
                    message == null || message.isBlank() ? code.getMessage() : message
            );
        }
    }

    private Throwable unwrap(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null
                && current.getCause() != current
                && (current instanceof RuntimeException || current.getClass().getName().contains("InvocationTargetException"))) {
            current = current.getCause();
        }
        return current;
    }

    private ResultCode resolveResultCode(Throwable throwable) {
        if (throwable == null) {
            return ResultCode.INTERNAL_ERROR;
        }
        try {
            Method method = throwable.getClass().getMethod("getResultCode");
            Object value = method.invoke(throwable);
            if (value instanceof ResultCode resultCode) {
                return resultCode;
            }
        } catch (Exception ignored) {
        }
        return ResultCode.INTERNAL_ERROR;
    }

    private byte resolveResponseVersion(byte requestVersion) {
        if (requestVersion == supportedProtocolVersion) {
            return requestVersion;
        }
        return supportedProtocolVersion;
    }
}
