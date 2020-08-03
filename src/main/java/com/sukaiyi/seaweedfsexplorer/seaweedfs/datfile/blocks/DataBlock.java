package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class DataBlock implements Block<byte[]> {

    public static final String NAME = "DataBlock";

    private byte[] data;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public long size(Map<Class<?>, Block<?>> blockAlreadyDecode) {
        return Optional.of(DataSizeBlock.class)
                .map(blockAlreadyDecode::get)
                .map(Block::getDecodedData)
                .filter(e -> e instanceof Long)
                .map(e -> (Long) e)
                .orElse(0L);
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
    }

    @Override
    public byte[] getDecodedData() {
        return data;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return FlagBlock.class;
    }
}
