package com.sukaiyi.seaweedfsexplorer.nettyhandler.httphandler;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public abstract class AbstractJsonHttpHandler<T> implements HttpHandler {

    private static final Gson GSON = new Gson();

    @Override
    public boolean handle(ChannelHandlerContext ctx, FullHttpRequest request) {
        T result = jsonResult(ctx, request);
        if (result == null) {
            return false;
        }
        String json = GSON.toJson(result);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        return true;
    }

    protected abstract T jsonResult(ChannelHandlerContext ctx, FullHttpRequest request);

}
