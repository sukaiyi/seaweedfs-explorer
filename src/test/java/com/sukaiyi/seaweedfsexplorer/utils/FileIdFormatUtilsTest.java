package com.sukaiyi.seaweedfsexplorer.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileIdFormatUtilsTest {

    @Test
    void format() {

        assertEquals("023a4000", FileIdFormatUtils.formatId("00023a4000"));
        assertEquals("023a400000", FileIdFormatUtils.formatId("0000023a400000"));
        assertEquals("023a4000000000", FileIdFormatUtils.formatId("0000023a4000000000"));
        assertEquals("023a40", FileIdFormatUtils.formatId("023a40"));

        assertEquals("00023a40", FileIdFormatUtils.formatCookie("00023a4000"));
        assertEquals("0000023a40", FileIdFormatUtils.formatCookie("0000023a400000"));
        assertEquals("0000023a40", FileIdFormatUtils.formatCookie("0000023a4000000000"));
        assertEquals("023a40", FileIdFormatUtils.formatCookie("023a40"));

    }
}