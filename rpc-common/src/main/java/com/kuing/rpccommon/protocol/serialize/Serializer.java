package com.kuing.rpccommon.protocol.serialize;

/**
 * 序列化接口
 */
public interface Serializer {
    /**
     * java对象转化为二进制
     *
     * @param object
     * @return
     * @throws Exception
     */
    byte[] serialize(Object object) throws Exception;

    /**
     * @param clazz
     * @param bytes
     * @return
     */
    Object deserialize(Class clazz, byte[] bytes);

}
