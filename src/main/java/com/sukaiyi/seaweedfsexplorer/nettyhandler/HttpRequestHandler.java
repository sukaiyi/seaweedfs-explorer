package com.sukaiyi.seaweedfsexplorer.nettyhandler;

import com.sukaiyi.seaweedfsexplorer.nettyhandler.httphandler.HttpHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Map<String, HttpHandler> HANDLERS = new HashMap<>();

    static {
        Properties properties = new Properties();
        try (InputStream in = HttpRequestHandler.class.getClassLoader().getResourceAsStream("mappings.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<String> keys = properties.stringPropertyNames();
        for (String key : keys) {
            String value = properties.getProperty(key);
            try {
                Class<?> clazz = Class.forName(key);
                Object obj = clazz.newInstance();
                if(obj instanceof HttpHandler){
                    HANDLERS.put(value, (HttpHandler) obj);
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (is100ContinueExpected(request)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }
        String uri = request.uri();
        Set<String> patterns = HANDLERS.keySet();
        boolean handled = false;
        for (String pattern : patterns) {
            if (uri.matches(pattern)) {
                handled = HANDLERS.get(pattern).handle(ctx, request);
                break;
            }
        }
        if (!handled) {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.NOT_FOUND
            );
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
