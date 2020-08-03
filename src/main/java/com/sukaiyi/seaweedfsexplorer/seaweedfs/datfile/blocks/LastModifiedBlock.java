package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class LastModifiedBlock implements Block<Instant> {

    public static final String NAME = "LastModifiedBlock";

    private Instant data;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public long size(Map<Class<?>, Block<?>> blockAlreadyDecode) {
        byte flag = Optional.of(FlagBlock.class)
                .map(blockAlreadyDecode::get)
                .map(Block::getDecodedData)
                .filter(e -> e instanceof Byte)
                .map(e -> (Byte) e)
                .orElse((byte) 0);
        if ((flag & 0x08) == 0) {
            return 0;
        }
        return 5;
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
        long lastModified = ByteUtils.byteToUnsignedLong(bytes, start, len);
        data = Instant.ofEpochSecond(lastModified);
    }

    @Override
    public Instant getDecodedData() {
        return data;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return PairSizeBlock.class;
    }
}
