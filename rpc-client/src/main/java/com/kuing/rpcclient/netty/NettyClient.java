package com.kuing.rpcclient.netty;

import com.kuing.rpccommon.protocol.RpcDecoder;
import com.kuing.rpccommon.protocol.RpcEncoder;
import com.kuing.rpccommon.protocol.RpcRequest;
import com.kuing.rpccommon.protocol.RpcResponse;
import com.kuing.rpccommon.protocol.serialize.JSONSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.util.Date;

@Slf4j
public class NettyClient {
    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private ClientHandler clientHandler;
    private String host;
    private Integer port;
    private static final int MAX_RETRY = 5;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        eventLoopGroup = new NioEventLoopGroup();
        clientHandler = new ClientHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4));
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(new RpcDecoder(RpcResponse.class, new JSONSerializer()));
                        pipeline.addLast(clientHandler);
                    }
                });
        connect(bootstrap, host, port, MAX_RETRY);

    }

    public void connect(Bootstrap bootstrap, String host, int port, int retry) {
        ChannelFuture channelFuture = bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("连接服务器成功");
            } else if (retry == 0) {
                log.error("重试次数已用完，放弃连接");

            } else {
                int order = MAX_RETRY - retry + 1;
                int delay = 1 << order;
                log.error("{}：连接失败，第{}重连...", new Date(), order);
//                bootstrap.config().group().schedule(() ->connect())
            }
        });
        channel = channelFuture.channel();

    }

    public RpcResponse send(final RpcRequest request) {
        try {
            channel.writeAndFlush(request).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clientHandler.getRpcResponse(request.getRequestId());
    }

    @PreDestroy
    public void close() {
        eventLoopGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
    }
}
