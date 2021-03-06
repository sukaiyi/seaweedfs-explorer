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
public class AppendAtNsBlock implements Block<Long> {

    public static final String NAME = "AppendAtNsBlock";

    private Long appendAtNs;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public long size(Map<Class<?>, Block<?>> blockAlreadyDecode) {
        int version = Optional.of(SuperBlock.class)
                .map(blockAlreadyDecode::get)
                .map(Block::getDecodedData)
                .filter(e -> e instanceof Integer)
                .map(e -> (Integer) e)
                .orElse(0);
        if (version >= 3) { // 版本 2 以下没有这个字段
            return 8;
        } else {
            return 0;
        }
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
        this.appendAtNs = ByteUtils.byteToUnsignedLong(bytes, start, len);
    }

    @Override
    public Long getDecodedData() {
        return appendAtNs;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        return PaddingBlock.class;
    }
}
