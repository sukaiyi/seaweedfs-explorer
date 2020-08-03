package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class TtlBlock implements Block<byte[]> {

    public static final String NAME = "TtlBlock";

    private byte[] data;

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
        if ((flag & 0x10) == 0) {
            return 0;
        }
        return 2;
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
        if (len >= 2) {
            data = new byte[2];
            System.arraycopy(bytes, start, data, 2, len);
        }
    }

    @Override
    public byte[] getDecodedData() {
        return data;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return CheckSumBlock.class;
    }
}
