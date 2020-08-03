package com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks.TotalSizeBlock;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.IdxFileModel;
import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class SizeBlock implements Block<Long> {

    public static final String NAME = "SizeBlock";
    public static final Integer SIZE = 4;

    private Long size;

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
        this.size = ByteUtils.byteToUnsignedLong(bytes, start, len);
    }

    @Override
    public Long getDecodedData() {
        return size;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        IdxFileModel idx = new IdxFileModel();
        idx.setId(Optional.of(IdBlock.class).map(blockAlreadyDecode::get).map(e -> e.getDecodedData()).map(e -> (String) e).orElse(null));
        idx.setOffset(Optional.of(OffsetBlock.class).map(blockAlreadyDecode::get).map(e -> e.getDecodedData()).map(e -> (Long) e).orElse(0L));
        idx.setSize(Optional.of(SizeBlock.class).map(blockAlreadyDecode::get).map(e -> e.getDecodedData()).map(e -> (Long) e).orElse(0L));

        if (idx.getSize() > 0 && idx.getOffset() > 0) {
            ret.add(idx);
        }

        blockAlreadyDecode.clear();
        return IdBlock.class;
    }
}
