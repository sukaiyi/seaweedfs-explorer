package com.sukaiyi.seaweedfsexplorer.utils;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class FileIdFormatUtils {

    public static String format(String fileId) {
        int length = fileId.length();
        int start = 0;
        while (start < length - 1 && fileId.charAt(start) == '0' && fileId.charAt(start + 1) == '0') {
            start += 2;
        }

        int end = fileId.length() - 1;
        while (end > 0 && fileId.charAt(end) == '0' && fileId.charAt(end - 1) == '0') {
            end -= 2;
        }
        return fileId.substring(start, end + 1);
    }
}
