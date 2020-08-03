package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class PairBlock implements Block<String> {

    public static final String NAME = "PairBlock";

    private String pair;
    private byte[] pairBytes;
    private int pairBytePos;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public long size(Map<Class<?>, Block<?>> blockAlreadyDecode) {
        int pairSize = Optional.of(PairSizeBlock.class)
                .map(blockAlreadyDecode::get)
                .map(Block::getDecodedData)
                .filter(e -> e instanceof Integer)
                .map(e -> (Integer) e)
                .orElse(0);

        if (pairBytes == null) {
            pairBytes = new byte[pairSize];
        }
        return pairSize;
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
        System.arraycopy(bytes, start, pairBytes, pairBytePos, len);
        pairBytePos += len;
        if (finished && pairBytePos > 0) {
            this.pair = new String(pairBytes, 0, pairBytePos, StandardCharsets.UTF_8);
        }
    }

    @Override
    public String getDecodedData() {
        return pair;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return TtlBlock.class;
    }
}
