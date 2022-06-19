package com.kuing.dubbohello;

import com.kuing.dubbohello.api.GreetingService;
import com.kuing.dubbohello.provider.GreetingServiceImpl;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.extension.Wrapper;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.registry.integration.RegistryProtocol;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;
import org.apache.dubbo.rpc.protocol.injvm.InjvmProtocol;

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class consumerApplication {
    private static String zookeeperHost = System.getProperty("zookeeper.address", "127.0.0.1");

    public static void main(String[] args) throws InterruptedException {
        //创建关联配置 要调用的接口仍为泛型
        ReferenceConfig<GreetingService> reference = new ReferenceConfig<>();
        //设置应用程序的名字
        reference.setApplication(new ApplicationConfig("first-dubbo-consumer"));
        //设置注册中心
        reference.setRegistry(new RegistryConfig("zookeeper://" + zookeeperHost + ":2181"));
        //设置要使用的接口
        reference.setInterface(GreetingService.class);
        //通过 get 获取远程接口调用
//        reference.setInjvm(true);
        GreetingService service = reference.get();
        String msg = service.sayHi("dubbo");
        System.out.println(msg);


    }
}
