package com.sukaiyi.seaweedfsexplorer.utils;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class MathUtils {

    private MathUtils() {

    }

    public static <T extends Comparable<T>> T min(T... ts) {
        return Arrays.stream(ts).min(Comparator.naturalOrder()).orElse(null);
    }

    public static <T extends Comparable<T>> T max(T... ts) {
        return Arrays.stream(ts).max(Comparator.naturalOrder()).orElse(null);
    }

}
