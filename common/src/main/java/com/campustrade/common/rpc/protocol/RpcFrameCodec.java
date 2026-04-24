package com.campustrade.common.rpc.protocol;

import com.campustrade.common.rpc.RpcRequest;
import com.campustrade.common.rpc.RpcResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Binary frame codec:
 * [magic:int][version:byte][serializer:byte][type:byte][bodyLength:int][body:bytes].
 */
public final class RpcFrameCodec {
    private RpcFrameCodec() {
    }

    public static void writeRequest(OutputStream output,
                                    RpcRequest request,
                                    byte protocolVersion,
                                    RpcSerializer serializer) throws IOException {
        byte[] body = serializer.serialize(request);
        writeFrame(output, protocolVersion, serializer.code(), RpcMessageType.REQUEST.getCode(), body);
    }

    public static void writeResponse(OutputStream output,
                                     RpcResponse response,
                                     byte protocolVersion,
                                     RpcSerializer serializer) throws IOException {
        byte[] body = serializer.serialize(response);
        writeFrame(output, protocolVersion, serializer.code(), RpcMessageType.RESPONSE.getCode(), body);
    }

    public static RpcTransportPacket<RpcRequest> readRequest(InputStream input) throws IOException, ClassNotFoundException {
        Frame frame = readFrame(input, RpcMessageType.REQUEST);
        RpcRequest request = frame.serializer.deserialize(frame.body, RpcRequest.class);
        return new RpcTransportPacket<>(frame.protocolVersion, frame.serializer, request);
    }

    public static RpcTransportPacket<RpcResponse> readResponse(InputStream input) throws IOException, ClassNotFoundException {
        Frame frame = readFrame(input, RpcMessageType.RESPONSE);
        RpcResponse response = frame.serializer.deserialize(frame.body, RpcResponse.class);
        return new RpcTransportPacket<>(frame.protocolVersion, frame.serializer, response);
    }

    private static void writeFrame(OutputStream output,
                                   byte protocolVersion,
                                   byte serializerCode,
                                   byte messageTypeCode,
                                   byte[] body) throws IOException {
        if (!RpcProtocol.isSupportedVersion(protocolVersion)) {
            throw new RpcProtocolException("Unsupported protocol version for write: " + protocolVersion);
        }
        if (body == null) {
            body = new byte[0];
        }
        if (body.length > RpcProtocol.MAX_BODY_LENGTH) {
            throw new RpcProtocolException("RPC frame body too large: " + body.length);
        }

        DataOutputStream dataOutput = new DataOutputStream(output);
        dataOutput.writeInt(RpcProtocol.MAGIC);
        dataOutput.writeByte(protocolVersion);
        dataOutput.writeByte(serializerCode);
        dataOutput.writeByte(messageTypeCode);
        dataOutput.writeInt(body.length);
        dataOutput.write(body);
        dataOutput.flush();
    }

    private static Frame readFrame(InputStream input, RpcMessageType expectedType) throws IOException {
        DataInputStream dataInput = new DataInputStream(input);

        int magic = dataInput.readInt();
        if (magic != RpcProtocol.MAGIC) {
            throw new RpcProtocolException("Invalid RPC magic: " + magic);
        }

        byte version = dataInput.readByte();
        if (!RpcProtocol.isSupportedVersion(version)) {
            throw new RpcProtocolException("Unsupported RPC protocol version: " + version);
        }

        byte serializerCode = dataInput.readByte();
        RpcSerializer serializer = RpcSerializerRegistry.requireByCode(serializerCode);

        byte messageTypeCode = dataInput.readByte();
        RpcMessageType actualType = RpcMessageType.fromCode(messageTypeCode);
        if (actualType != expectedType) {
            throw new RpcProtocolException(
                    "Unexpected RPC message type. expected=" + expectedType + ", actual=" + actualType
            );
        }

        int bodyLength = dataInput.readInt();
        if (bodyLength < 0 || bodyLength > RpcProtocol.MAX_BODY_LENGTH) {
            throw new RpcProtocolException("Invalid RPC body length: " + bodyLength);
        }

        byte[] body = new byte[bodyLength];
        dataInput.readFully(body);
        return new Frame(version, serializer, body);
    }

    private record Frame(byte protocolVersion, RpcSerializer serializer, byte[] body) {
    }
}

