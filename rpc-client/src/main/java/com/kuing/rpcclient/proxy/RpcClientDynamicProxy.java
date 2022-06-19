package com.kuing.rpcclient.proxy;

import com.esotericsoftware.minlog.Log;
import com.kuing.rpcclient.netty.NettyClient;
import com.kuing.rpccommon.protocol.RpcRequest;
import com.kuing.rpccommon.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class RpcClientDynamicProxy<T> implements InvocationHandler {
    private Class<T> clazz;

    public RpcClientDynamicProxy(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        String requestId = UUID.randomUUID().toString();

        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();

        Class<?>[] parameterTypes = method.getParameterTypes();

        request.setRequestId(requestId);
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);
        request.setParameterTypes(parameterTypes);
        log.info("请求内容：{}", request);
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8888);
        log.info("开始连接服务端：{}", new Date());
        nettyClient.connect();
        RpcResponse response = nettyClient.send(request);
        log.info("请求调用结果：{}", response.getResult());
        return response.getResult();
    }
}
