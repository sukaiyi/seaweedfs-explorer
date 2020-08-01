package com.sukaiyi.seaweedfsexplorer.utils;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class FileIdFormatUtils {

    public static String formatId(String id) {
        int length = id.length();
        int start = 0;
        while (start < length - 1 && id.charAt(start) == '0' && id.charAt(start + 1) == '0') {
            start += 2;
        }


        return id.substring(start);
    }

    public static String formatCookie(String cookie) {
        int end = cookie.length() - 1;
        while (end > 0 && cookie.charAt(end) == '0' && cookie.charAt(end - 1) == '0') {
            end -= 2;
        }
        if (end <= 0) {
            return "";
        }
        return cookie.substring(0, end + 1);
    }


}
