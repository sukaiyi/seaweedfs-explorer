package com.sukaiyi.seaweedfsexplorer.nettyserver;

import com.sukaiyi.seaweedfsexplorer.channelinitialzer.HttpChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author sukaiyi
 * @date 2020/06/12
 */
@Slf4j
public class ServerBootstrapConfig {

    private static final int PORT = 35672;

    public ServerBootstrap httpServerBootstrap() {
        return setupServer(HttpChannelInitializer::new, future -> {
            if (future.isSuccess()) {
                log.info("httpServer start at port {} success.\n visit http://127.0.0.1:{}/index.html", PORT, PORT);
            } else {
                Throwable cause = future.cause();
                log.error("httpServer start at port {} failed", PORT, cause);
            }
        });
    }

    private ServerBootstrap setupServer(Supplier<ChannelInitializer<SocketChannel>> initializerSupplier, Consumer<Future<?>> resultConsumer) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(initializerSupplier.get())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
        serverBootstrap.bind(PORT).addListener(resultConsumer::accept);
        return serverBootstrap;
    }
}
