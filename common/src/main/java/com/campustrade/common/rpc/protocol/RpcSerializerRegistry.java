package com.campustrade.common.rpc.protocol;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serializer registry for protocol extensibility.
 */
public final class RpcSerializerRegistry {
    private static final Map<Byte, RpcSerializer> SERIALIZER_BY_CODE = new ConcurrentHashMap<>();
    private static final Map<String, RpcSerializer> SERIALIZER_BY_NAME = new ConcurrentHashMap<>();

    static {
        register(new JavaRpcSerializer());
    }

    private RpcSerializerRegistry() {
    }

    public static void register(RpcSerializer serializer) {
        if (serializer == null) {
            throw new IllegalArgumentException("serializer cannot be null");
        }
        SERIALIZER_BY_CODE.put(serializer.code(), serializer);
        SERIALIZER_BY_NAME.put(normalizeName(serializer.name()), serializer);
    }

    public static Optional<RpcSerializer> findByCode(byte code) {
        return Optional.ofNullable(SERIALIZER_BY_CODE.get(code));
    }

    public static Optional<RpcSerializer> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(SERIALIZER_BY_NAME.get(normalizeName(name)));
    }

    public static RpcSerializer requireByCode(byte code) throws RpcProtocolException {
        RpcSerializer serializer = SERIALIZER_BY_CODE.get(code);
        if (serializer == null) {
            throw new RpcProtocolException("Unsupported serializer code: " + code);
        }
        return serializer;
    }

    public static RpcSerializer requireByName(String name) throws RpcProtocolException {
        String normalized = normalizeName(name);
        RpcSerializer serializer = SERIALIZER_BY_NAME.get(normalized);
        if (serializer == null) {
            throw new RpcProtocolException("Unsupported serializer name: " + name);
        }
        return serializer;
    }

    private static String normalizeName(String name) {
        return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
    }
}

