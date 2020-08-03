package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class NameSizeBlock implements Block<Integer> {

    public static final String NAME = "NameSizeBlock";

    private int nameSize;

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
        if ((flag & 0x02) == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
        this.nameSize = (int) ByteUtils.byteToUnsignedLong(bytes, start, len);
    }

    @Override
    public Integer getDecodedData() {
        return nameSize;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return NameBlock.class;
    }
}
