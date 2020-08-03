package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.DatFileModel;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class PaddingBlock implements Block<Instant> {

    public static final String NAME = "PaddingBlock";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public long size(Map<Class<?>, Block<?>> blockAlreadyDecode) {
        // 按 8 字节对齐
        long totalSize = blockAlreadyDecode.entrySet().stream()
                .filter(e -> e.getKey() != SuperBlock.class)
                .filter(e -> e.getKey() != PaddingBlock.class)
                .map(Map.Entry::getValue)
                .mapToLong(e -> e.size(blockAlreadyDecode))
                .sum();
        return 8 - totalSize % 8;
    }

    @Override
    public void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished) {
    }

    @Override
    public Instant getDecodedData() {
        return null;
    }

    @Override
    @SuppressWarnings("all")
    public Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret) {
        DatFileModel dat = new DatFileModel();
        dat.setCookie(Optional.of(CookieBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (String) e).orElse(null));
        dat.setId(Optional.of(IdBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (String) e).orElse(null));
        dat.setSize(Optional.of(TotalSizeBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (Long) e).orElse(0L));
        dat.setDataSize(Optional.of(DataSizeBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (Long) e).orElse(0L));
        dat.setData(Optional.of(DataBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (byte[]) e).orElse(null));
        dat.setFlags(Optional.of(FlagBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (Byte) e).orElse((byte) 0));
        dat.setNameSize(Optional.of(NameSizeBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (Integer) e).orElse(0));
        dat.setName(Optional.of(NameBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (String) e).orElse(null));
        dat.setMimeSize(Optional.of(MimeSizeBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (Integer) e).orElse(0));
        dat.setMime(Optional.of(MimeBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (String) e).orElse(null));
        dat.setPairsSize(Optional.of(PairSizeBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (Integer) e).orElse(0));
        dat.setPairs(Optional.of(PairBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (String) e).orElse(null));
        dat.setLastModified(Optional.of(LastModifiedBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (Instant) e).map(e -> e.toEpochMilli() / 1000).orElse(0L));
        dat.setTtl(Optional.of(TtlBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (byte[]) e).orElse(null));
        dat.setCheckSum(Optional.of(CheckSumBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (String) e).orElse(null));
        dat.setAppendAtNs(Optional.of(AppendAtNsBlock.class).map(blockAlreadyDecode::get).map(Block::getDecodedData).map(e -> (Instant) e).map(e -> e.toEpochMilli()).orElse(0L));
        ret.add(dat);

        Block superBlock = blockAlreadyDecode.get(SuperBlock.class);
        blockAlreadyDecode.clear();
        blockAlreadyDecode.put(SuperBlock.class, superBlock);

        return CookieBlock.class;
    }
}
