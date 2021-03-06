package com.sukaiyi.seaweedfsexplorer.nettyhandler.httphandler;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.DatFileModel;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.StateBasedDatFileAnalyzer;
import com.sukaiyi.seaweedfsexplorer.utils.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class DatFileHttpHandler extends AbstractJsonHttpHandler<DatFileHttpHandler.LayUITableData<DatFileHttpHandler.DatFileModelForLayUI>> {

    public static final Map<String, List<DatFileModel>> CACHE = new HashMap<>();

    @Override
    protected LayUITableData<DatFileModelForLayUI> jsonResult(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        Map<String, String> paramMap = parseParam(uri);
        if (paramMap == null) {
            return null;
        }
        String workPath = WorkDirUtils.getWorkPath();
        String fileName = paramMap.get("file");
        if (fileName == null || fileName.isEmpty()) {
            LayUITableData<DatFileModelForLayUI> ret = new LayUITableData<>();
            ret.setCode(0);
            return ret;
        }
        String datPath = workPath + File.separator + fileName;
        boolean refresh = Boolean.parseBoolean(paramMap.get("refresh"));
        if (CACHE.get(datPath) == null || refresh) {
            List<DatFileModel> data = new StateBasedDatFileAnalyzer().exec(datPath);
            CACHE.put(datPath, data);
        }
        List<DatFileModel> total = CACHE.get(datPath);

        int page = Integer.parseInt(paramMap.getOrDefault("page", "1"));
        int limit = Integer.parseInt(paramMap.getOrDefault("limit", "15"));
        String sortBy = paramMap.getOrDefault("sortBy", "id");
        String order = paramMap.get("order");
        if (order == null || order.isEmpty()) {
            sortBy = "id";
            order = "desc";
        }
        String volumeId = Optional.of(fileName).map(e -> e.substring(0, e.lastIndexOf(".dat"))).orElse("");
        List<DatFileModelForLayUI> data = total.stream()
                .sorted(ComparatorFactory.build(sortBy, order))
                .skip((long) (page - 1) * limit)
                .limit(limit)
                .map(this::convert)
                .peek(e -> e.setFileId(volumeId + "," + e.getFileId()))
                .peek(e -> e.setDatFile(fileName))
                .collect(Collectors.toList());
        LayUITableData<DatFileModelForLayUI> ret = new LayUITableData<>();
        ret.setCode(0);
        ret.setCount(total.size());
        ret.setData(data);
        return ret;
    }

    private DatFileModelForLayUI convert(DatFileModel datFileModel) {
        DatFileModelForLayUI ret = new DatFileModelForLayUI();
        ret.setFileId(datFileModel.getId() + datFileModel.getCookie());
        ret.setId(datFileModel.getId());
        ret.setDataSize(DataSizeUtils.humanize(datFileModel.getDataSize()));
        ret.setFlags(datFileModel.getFlags());
        ret.setName(datFileModel.getName());
        ret.setPairs(datFileModel.getPairs());
        ret.setCheckSum(datFileModel.getCheckSum());
        ret.setMime(datFileModel.getMime());
        ret.setSupportPreview(ContentTypeUtils.SUPPORT_PREVIEW.contains(datFileModel.getMime()));
        ret.setTtl(Arrays.toString(datFileModel.getTtl()));
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(datFileModel.getLastModified(), 0, ZoneOffset.ofHours(0));
        String lastModified = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
        ret.setLastModified(lastModified);
        return ret;
    }

    private Map<String, String> parseParam(String uri) {
        if (!uri.matches("/datfile\\?.*")) {
            return null;
        }
        String paramPart = uri.substring("/datfile?".length());
        String[] paramParts = paramPart.split("&");
        return Arrays.stream(paramParts)
                .map(e -> e.split("="))
                .filter(e -> e.length == 2)
                .collect(Collectors.toMap(e -> e[0], e -> e[1]));
    }

    @Data
    public static final class DatFileModelForLayUI {
        private String datFile;
        private String fileId;
        private String id;
        private String dataSize;
        private byte flags;
        private String name;
        private String mime;
        private Boolean supportPreview;
        private String pairs;
        private String lastModified;
        private String ttl;
        private String checkSum;
    }


    @Data
    public static final class LayUITableData<T> {
        private Integer code;
        private String msg;
        private Integer count;
        private List<T> data;
    }
}
