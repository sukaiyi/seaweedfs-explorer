package com.sukaiyi.seaweedfsexplorer.nettyhandler.httphandler;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.DatFileModel;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.IdxFileAnalyzer;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.IdxFileModel;
import com.sukaiyi.seaweedfsexplorer.utils.ContentTypeUtils;
import com.sukaiyi.seaweedfsexplorer.utils.UrlEncoder;
import com.sukaiyi.seaweedfsexplorer.utils.WorkDirUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;

import static com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.DatFileAnalyzer.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author sukaiyi
 * @date 2020/08/01
 */
public class FileDownloadHttpHandler implements HttpHandler {

    /**
     * (file, (id, IdxFileModel))
     */
    private final Map<String, Map<String, IdxFileModel>> cache = new HashMap<>();

    @Override
    public boolean handle(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        Map<String, String> paramMap = parseParam(uri);
        if (paramMap == null) {
            return false;
        }
        String workPath = WorkDirUtils.getWorkPath();
        String fileName = paramMap.get("file");
        String id = paramMap.get("id");

        String datFile = workPath + File.separator + fileName;
        String idxFile = datFile.substring(0, datFile.length() - ".dat".length()) + ".idx";

        if (cache.get(idxFile) == null) {
            List<IdxFileModel> data = new IdxFileAnalyzer().exec(idxFile);
            cache.put(idxFile, data.stream().collect(Collectors.toMap(IdxFileModel::getId, e -> e)));
        }
        Map<String, IdxFileModel> idxFileMap = cache.get(idxFile);
        IdxFileModel idx = idxFileMap.get(id);
        if (idx == null) {
            return false;
        }

        // find DatFileModel
        List<DatFileModel> datFileModels = DatFileHttpHandler.CACHE.get(datFile);
        DatFileModel dat = datFileModels.stream()
                .filter(e -> Objects.equals(e.getId(), id))
                .filter(e -> e.getSize() > 0)
                .filter(e -> e.getDataSize() > 0)
                .findFirst()
                .orElse(null);
        if (dat == null) {
            return false;
        }

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, dat.getDataSize());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, Optional.of(dat)
                .map(DatFileModel::getName)
                .map(String::toLowerCase)
                .map(ContentTypeUtils::chooseForFileName)
                .orElse("application/octet-stream"));
        response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, String.format("filename=\"%s\"", Optional.of(dat)
                .map(DatFileModel::getName)
                .map(UrlEncoder::encode)
                .orElse("unknown")));
        ctx.write(response);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(datFile, "r");
            ctx.write(new ChunkedFile(
                    randomAccessFile,
                    idx.getOffset() + COOKIE_SIZE + ID_SIZE + TOTAL_SIZE_SIZE + DATA_SIZE_SIZE,
                    dat.getDataSize(),
                    8192
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        return true;
    }

    private Map<String, String> parseParam(String uri) {
        if (!uri.matches("/download\\?.*")) {
            return null;
        }
        String paramPart = uri.substring("/download?".length());
        String[] paramParts = paramPart.split("&");
        return Arrays.stream(paramParts)
                .map(e -> e.split("="))
                .filter(e -> e.length == 2)
                .collect(Collectors.toMap(e -> e[0], e -> e[1]));
    }
}
