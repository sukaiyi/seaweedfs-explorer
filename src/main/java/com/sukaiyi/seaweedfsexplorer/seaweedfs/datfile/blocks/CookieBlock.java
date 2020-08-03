package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;
import com.sukaiyi.seaweedfsexplorer.utils.FileIdFormatUtils;

import java.util.List;
import java.util.Map;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class CookieBlock implements Block<String> {

    public static final String NAME = "CookieBlock";
    public static final Integer SIZE = 8;

    private String cookie;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public long size(Map<Class<?>, Block<?>> blockAlreadyDecode) {
        return SIZE;
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
        String cookie = ByteUtils.byteToHexString(bytes, start, len);
        this.cookie = FileIdFormatUtils.formatCookie(cookie);
    }

    @Override
    public String getDecodedData() {
        return cookie;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return IdBlock.class;
    }
}
