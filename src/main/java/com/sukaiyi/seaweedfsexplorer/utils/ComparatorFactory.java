package com.sukaiyi.seaweedfsexplorer.utils;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class ComparatorFactory {

    @SuppressWarnings("all")
    public static <T> Comparator<T> build(String sortBy, String order) {
        return (o1, o2) -> {
            Comparable o1Value = (Comparable) ReflectUtils.getFieldValue(o1, sortBy);
            Comparable o2Value = (Comparable) ReflectUtils.getFieldValue(o2, sortBy);

            Comparator<Comparable> comparator = (c1, c2) -> {
                return Objects.equals(order, "asc") ? c1.compareTo(c2) : c2.compareTo(c1);
            };
            boolean nullsFirst = Objects.equals(order, "asc");
            return (nullsFirst ? Comparator.<Comparable>nullsFirst(comparator) : Comparator.<Comparable>nullsLast(comparator)).compare(o1Value, o2Value);
        };
    }

}
