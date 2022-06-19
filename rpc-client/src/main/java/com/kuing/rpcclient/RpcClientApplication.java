package com.kuing.rpcclient;

import com.kuing.rpcclient.proxy.ProxyFactory;
import com.kuing.rpccommon.service.HelloSerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Proxy;

@SpringBootApplication
@Slf4j
public class RpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcClientApplication.class, args);
        HelloSerivce helloSerivce = null;
        try {
            helloSerivce = ProxyFactory.create(HelloSerivce.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("响应结果:{}", helloSerivce.hello("kuing"));
    }

}
