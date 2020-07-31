package com.sukaiyi.seaweedfsexplorer.nettyhandler.httphandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public interface HttpHandler {
    boolean handle(ChannelHandlerContext ctx, FullHttpRequest request);
}
