package com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks.TotalSizeBlock;
import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;
import com.sukaiyi.seaweedfsexplorer.utils.FileIdFormatUtils;

import java.util.List;
import java.util.Map;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class OffsetBlock implements Block<Long> {

    public static final String NAME = "OffsetBlock";
    public static final Integer SIZE = 4;

    private static final int NEEDLE_PADDING_SIZE = 8;

    private Long offset;

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
        this.offset = ByteUtils.byteToUnsignedLong(bytes, start, len) * NEEDLE_PADDING_SIZE;
    }

    @Override
    public Long getDecodedData() {
        return offset;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return SizeBlock.class;
    }
}
