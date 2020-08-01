package com.sukaiyi.seaweedfsexplorer.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class UrlEncoder {

    public static String encode(String s, Charset charset) {
        try {
            return URLEncoder.encode(s, charset.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String encode(String s) {
        return encode(s, StandardCharsets.UTF_8);
    }

}
