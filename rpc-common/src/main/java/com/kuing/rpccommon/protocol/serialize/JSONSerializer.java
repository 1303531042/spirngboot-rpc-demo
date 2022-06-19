package com.kuing.rpccommon.protocol.serialize;

public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) throws Exception {
        return new byte[0];
    }

    @Override
    public Object deserialize(Class clazz, byte[] bytes) {
        return null;
    }
}
