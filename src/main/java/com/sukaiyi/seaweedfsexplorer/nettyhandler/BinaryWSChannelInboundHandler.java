package com.sukaiyi.seaweedfsexplorer.nettyhandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinaryWSChannelInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        Channel currentChannel = ctx.channel();
        EventExecutor eventExecutor = ctx.executor();
        String content = frame.text();
        log.info(content);
    }
}
