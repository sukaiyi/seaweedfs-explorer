package com.sukaiyi.seaweedfsexplorer.utils;

import java.io.*;
import java.util.*;

/**
 * @author sukaiyi
 */
public class ContentTypeUtils {

    public static final Set<String> SUPPORT_PREVIEW = new HashSet<>(Arrays.asList(
            "text/plain", "image/jpeg", "application/x-jpg", "text/html", "video/mpeg4", "text/xml", "application/pdf", "application/json"
    ));

    private static final String UNKNOWN = "application/octet-stream";

    private static Map<String, String> contentTypeSuffixMap;

    private ContentTypeUtils() {

    }

    /**
     * 根据文件名选择合适的 Content-Type
     *
     * @param fileName 文件名
     * @return Content-Type
     */
    public static String chooseForFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ".";
        }
        String suffix = fileName.substring(lastDotIndex);

        if (contentTypeSuffixMap != null) {
            return contentTypeSuffixMap.getOrDefault(suffix, UNKNOWN);
        }
        synchronized (ContentTypeUtils.class) {
            if (contentTypeSuffixMap != null) {
                return contentTypeSuffixMap.getOrDefault(suffix, UNKNOWN);
            }
            return initContentTypeSuffixMap().getOrDefault(suffix, UNKNOWN);
        }
    }

    private static Map<String, String> initContentTypeSuffixMap() {
        contentTypeSuffixMap = new HashMap<>();

        try (InputStream is = ContentTypeUtils.class.getClassLoader().getResourceAsStream("content-type.map")) {
            if (is == null) {
                return contentTypeSuffixMap;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(":");
                contentTypeSuffixMap.put(data[0], data[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentTypeSuffixMap;
    }

}
