package com.sukaiyi.seaweedfsexplorer.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileIdFormatUtilsTest {

    @Test
    void format() {

        assertEquals("023a40", FileIdFormatUtils.format("00023a4000"));
        assertEquals("023a40", FileIdFormatUtils.format("0000023a400000"));
        assertEquals("023a40", FileIdFormatUtils.format("0000023a4000000000"));
        assertEquals("023a40", FileIdFormatUtils.format("023a40"));

    }
}