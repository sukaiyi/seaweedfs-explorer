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

    private static final Map<String, String> RESPONSE_CACHE = new HashMap<>();

    @Override
    public boolean handle(ChannelHandlerContext ctx, FullHttpRequest request) {
        boolean cacheable = cacheable();
        String resource = resource();
        String cachedResponse = RESPONSE_CACHE.get(resource);

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
                    RESPONSE_CACHE.put(resource, cachedResponse);
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

    /**
     * 指定该静态资源的访问路径
     *
     * @return 静态资源相对于resource目录的路径
     */
    protected abstract String resource();

    /**
     * 该资源是否支持缓存，如果不支持缓存每次 http 请求时都会通过{@link ClassLoader#getResourceAsStream(String)}获取数据
     *
     * @return 该资源是否支持缓存
     */
    protected abstract boolean cacheable();
}
