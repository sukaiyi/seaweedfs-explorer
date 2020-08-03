package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;

import java.util.List;
import java.util.Map;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class TotalSizeBlock implements Block<Long> {

    public static final String NAME = "TotalSizeBlock";
    public static final Integer SIZE = 4;

    private long totalSize;

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
        this.totalSize = ByteUtils.byteToUnsignedLong(bytes, start, len);
    }

    @Override
    public Long getDecodedData() {
        return totalSize;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return totalSize > 0 ? DataSizeBlock.class : CheckSumBlock.class;
    }
}
