package com.sukaiyi.seaweedfsexplorer.nettyhandler.httphandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public abstract class AbstractStaticHttpHandler implements HttpHandler {

    private static final Map<String, String> responseCache = new HashMap<>();

    @Override
    public boolean handle(ChannelHandlerContext ctx, FullHttpRequest request) {
        boolean cacheable = cacheable();
        String resource = resource();
        String cachedResponse = responseCache.get(resource);

        if (!cacheable || cachedResponse == null) {
            try (InputStream is = AbstractStaticHttpHandler.class.getClassLoader().getResourceAsStream(resource())) {
                if (is == null) {
                    return false;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                cachedResponse = sb.toString();
                if (cacheable) {
                    responseCache.put(resource, cachedResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cachedResponse != null) {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(cachedResponse, CharsetUtil.UTF_8));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return true;
        } else {
            return false;
        }
    }

    protected abstract String resource();

    protected abstract boolean cacheable();
}
