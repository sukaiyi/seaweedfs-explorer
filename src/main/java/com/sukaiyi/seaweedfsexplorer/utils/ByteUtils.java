package com.sukaiyi.seaweedfsexplorer.utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ByteUtils {

    public static String byteToHexString(byte[] bytes, int start, int len) {
        return IntStream.range(start, start + len)
                .mapToObj(e -> String.format("%02x", bytes[e]))
                .collect(Collectors.joining());
    }

    public static long byteToUnsignedLong(byte[] bytes, int start, int len) {
        return IntStream.range(start, start + len)
                .mapToLong(e -> Byte.toUnsignedInt(bytes[e]))
                .reduce(0, (left, right) -> left * 256 + right);
    }

}
