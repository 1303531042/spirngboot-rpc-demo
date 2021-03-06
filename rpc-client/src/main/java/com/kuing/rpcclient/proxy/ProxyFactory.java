package com.kuing.rpcclient.proxy;

import java.lang.reflect.Proxy;

public class ProxyFactory {
    public static <T> T create(Class<T> interfaceClass) throws Exception {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new RpcClientDynamicProxy<T>(interfaceClass));
    }
}
