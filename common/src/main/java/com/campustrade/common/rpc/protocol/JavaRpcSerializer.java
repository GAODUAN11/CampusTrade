package com.campustrade.common.rpc.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Java built-in object serializer.
 */
public class JavaRpcSerializer implements RpcSerializer {
    public static final byte CODE = 1;
    public static final String NAME = "java";

    @Override
    public byte code() {
        return CODE;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(byteArrayOutput)) {
            output.writeObject(value);
            output.flush();
            return byteArrayOutput.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] payload, Class<T> targetType) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(payload))) {
            Object value = input.readObject();
            if (value == null) {
                return null;
            }
            if (!targetType.isInstance(value)) {
                throw new IOException(
                        "Deserialized object type mismatch. expected="
                                + targetType.getName() + ", actual=" + value.getClass().getName()
                );
            }
            return (T) value;
        }
    }
}

