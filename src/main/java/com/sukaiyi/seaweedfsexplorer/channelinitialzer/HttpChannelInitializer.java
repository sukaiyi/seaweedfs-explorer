package com.sukaiyi.seaweedfsexplorer.channelinitialzer;

import com.sukaiyi.seaweedfsexplorer.nettyhandler.HttpRequestHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author sukaiyi
 * @date 2020/06/12
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {


    public HttpChannelInitializer() {
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline()
                .addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(512 * 1024))
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpRequestHandler());
    }
}
