package com.sukaiyi.seaweedfsexplorer.utils;

import java.util.List;
import java.util.function.Function;

public class BinarySearchUtils {

    private BinarySearchUtils() {

    }

    public static <T, K extends Comparable<K>> T search(List<T> data, K lookFor, Function<T, K> kMapping) {
        if (data == null) {
            return null;
        }
        int low = 0;
        int high = data.size() - 1;
        int middle = 0;

        if (low > high || lookFor.compareTo(kMapping.apply(data.get(low))) < 0 || lookFor.compareTo(kMapping.apply(data.get(high))) > 0) {
            return null;
        }

        while (low <= high) {
            middle = (low + high) / 2;
            int compareToMiddle = lookFor.compareTo(kMapping.apply(data.get(middle)));
            if (compareToMiddle < 0) {
                high = middle - 1;
            } else if (compareToMiddle > 0) {
                low = middle + 1;
            } else {
                return data.get(middle);
            }
        }
        return null;
    }


}
