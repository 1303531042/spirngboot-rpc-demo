package com.kuing.rpcserver.netty;

import com.kuing.rpccommon.protocol.RpcDecoder;
import com.kuing.rpccommon.protocol.RpcEncoder;
import com.kuing.rpccommon.protocol.RpcRequest;
import com.kuing.rpccommon.protocol.RpcResponse;
import com.kuing.rpccommon.protocol.serialize.JSONSerializer;
import com.kuing.rpccommon.registry.ServiceRegistry;
import com.kuing.rpccommon.registry.zookeeper.ZkServiceRegistry;
import com.kuing.rpcserver.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class NettyServer implements InitializingBean {
    private EventLoopGroup boss = null;
    private EventLoopGroup worker = null;
    @Autowired
    private ServerHandler serverHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceRegistry registry = new ZkServiceRegistry("127.0.0.1:2181");
        start(registry);

    }

    public void start(ServiceRegistry registry) throws Exception {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4));
                        pipeline.addLast(new RpcEncoder(RpcResponse.class, new JSONSerializer()));
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(serverHandler);
                    }
                });
        bind(serverBootstrap, 8888);
//        registry.registry("127.0.0.1:8888");
    }

    private void bind(final ServerBootstrap serverBootstrap, int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("端口[{}]绑定成功", port);
            } else {
                log.info("端口[{}]绑定失败", port);

                bind(serverBootstrap, port + 1);
            }
        });
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        boss.shutdownGracefully().sync();
        worker.shutdownGracefully().sync();
    }
}
