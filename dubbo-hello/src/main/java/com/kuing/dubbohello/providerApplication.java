package com.kuing.dubbohello;

import com.kuing.dubbohello.api.GreetingService;
import com.kuing.dubbohello.provider.GreetingServiceImpl;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.spring.schema.DubboNamespaceHandler;

import java.util.concurrent.CountDownLatch;

public class providerApplication {
    private static String zookeeperHost = System.getProperty("zookeeper.address", "127.0.0.1");

    public static void main(String[] args) throws InterruptedException {
        System.out.println(zookeeperHost);
        //创建服务配合 服务对应的接口类 设置为泛型
        ServiceConfig<GreetingService> service = new ServiceConfig<>();
        //设置应用程序的名字
        service.setApplication(new ApplicationConfig("first-dubbo-provider"));
        //设置zookeeper
        service.setRegistry(new RegistryConfig("zookeeper://" + zookeeperHost + ":2181"));

        //设置提供的接口 设置具体实现类
        service.setInterface(GreetingService.class);
        service.setRef(new GreetingServiceImpl());

        //设置完成 执行导出命令
        service.export();

        //等待客户端连接  挂起线程
        new CountDownLatch(1).await();
    }
}
