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
public class NameBlock implements Block<String> {

    public static final String NAME = "NameBlock";

    private String name;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public long size(Map<Class<?>, Block<?>> blockAlreadyDecode) {
        return Optional.of(NameSizeBlock.class)
                .map(blockAlreadyDecode::get)
                .map(Block::getDecodedData)
                .filter(e -> e instanceof Integer)
                .map(e -> (Integer) e)
                .orElse(0);
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
        this.name = new String(bytes, start, len, StandardCharsets.UTF_8);
    }

    @Override
    public String getDecodedData() {
        return name;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return MimeSizeBlock.class;
    }
}
