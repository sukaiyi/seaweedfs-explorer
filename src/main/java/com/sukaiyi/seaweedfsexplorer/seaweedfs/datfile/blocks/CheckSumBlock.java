package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;

import java.util.List;
import java.util.Map;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class CheckSumBlock implements Block<String> {

    public static final String NAME = "CheckSumBlock";

    private String checkSum;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public long size(Map<Class<?>, Block<?>> blockAlreadyDecode) {
        return 4;
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
        this.checkSum = ByteUtils.byteToHexString(bytes, start, len);
    }

    @Override
    public String getDecodedData() {
        return checkSum;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return AppendAtNsBlock.class;
    }
}
