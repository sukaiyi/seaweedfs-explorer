package com.sukaiyi.seaweedfsexplorer.utils;

import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/01/10
 */
public class DataSizeUtils {

    /**
     * 单位
     */
    private static final String[] UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB", "NB", "DB"};
    /**
     * 单位进位
     */
    private static final long CARRY_BIT = 1024;

    private DataSizeUtils() {

    }

    public static String humanize(long dataSize) {
        double dataSizeEnsured = Optional.of(dataSize).filter(e -> e >= 0).orElse(0L);
        int i = 0;
        for (; dataSizeEnsured >= CARRY_BIT && i < UNITS.length - 1; i++) {
            dataSizeEnsured = dataSizeEnsured / CARRY_BIT;
        }
        return String.format("%.2f %s", dataSizeEnsured, UNITS[i]);
    }

}
