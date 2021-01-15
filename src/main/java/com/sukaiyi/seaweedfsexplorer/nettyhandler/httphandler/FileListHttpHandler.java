package com.sukaiyi.seaweedfsexplorer.nettyhandler.httphandler;

import com.sukaiyi.seaweedfsexplorer.utils.WorkDirUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class FileListHttpHandler extends AbstractJsonHttpHandler<List<String>> {

    @Override
    protected List<String> jsonResult(ChannelHandlerContext ctx, FullHttpRequest request) {
        File workDir = WorkDirUtils.getWorkDir();
        File[] files = workDir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files)
                .map(File::getName)
                .filter(e -> e.endsWith(".dat"))
                .collect(Collectors.toList());
    }
}
