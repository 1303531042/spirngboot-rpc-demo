package com.kuing.rpcserver.handler;

import com.kuing.rpccommon.protocol.RpcRequest;
import com.kuing.rpccommon.protocol.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMember;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import java.lang.reflect.InvocationTargetException;

@Component
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResponseId(rpcRequest.getRequestId());
        try {
            Object handler = handler(rpcRequest);
            log.info("获取返回结果：{}", handler);
            rpcResponse.setResult(handler);
        } catch (Throwable throwable) {
            rpcResponse.setError(throwable.toString());
            throwable.printStackTrace();
        }
        channelHandlerContext.writeAndFlush(rpcResponse);
    }

    private Object handler(RpcRequest rpcRequest) throws ClassNotFoundException, InvocationTargetException {
        Class<?> clazz = Class.forName(rpcRequest.getClassName());
        Object serviceBean = applicationContext.getBean(clazz);
        log.info("serviceBean:{}", serviceBean);
        Class<?> serviceClass = serviceBean.getClass();
        log.info("serviceClass:{}", serviceClass);
        String methodName = rpcRequest.getMethodName();

        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();

        //使用CGLIB reflect
        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod method = fastClass.getMethod(methodName, parameterTypes);
        log.info("开始调用CGLIB动态代理执行服务端方法...");
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}
