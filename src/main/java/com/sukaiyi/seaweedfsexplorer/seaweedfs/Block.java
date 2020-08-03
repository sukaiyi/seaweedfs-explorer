package com.sukaiyi.seaweedfsexplorer.seaweedfs;

import java.util.List;
import java.util.Map;

/**
 * 数据块抽象
 *
 * @author sukaiyi
 * @date 2020/08/03
 */
public interface Block<T> {

    /**
     * @return 用于肉眼识别的名字，目前没用到
     */
    String name();

    /**
     * 该数据块的大小， 可以根据已经读取的数据块计算
     *
     * @param blockAlreadyDecode 已经读取到的数据块
     * @return 该数据块的大小
     */
    long size(Map<Class<?>, Block<?>> blockAlreadyDecode);

    /**
     * 解析该数据块的内容，当数据块比较长时，可能会多次回调这个方法
     * 具体多长时会多次回调，取决于 {@link StateBasedFileAnalyzer} 中缓冲区的长度
     *
     * @param blockAlreadyDecode 已经读取到的数据块
     * @param bytes              该数据块的字节数组
     * @param start              字节数组起始位置
     * @param len                数据长度
     * @param finished           该数据块是否已经读取完了
     */
    void decode(Map<Class<?>, Block<?>> blockAlreadyDecode, byte[] bytes, int start, int len, boolean finished);

    /**
     * @return 该数据块解析出的数据
     */
    T getDecodedData();

    /**
     * 下一个读取的数据块，可以根据已经读取的数据块计算
     *
     * @param blockAlreadyDecode 已经读取到的数据块
     * @param ret                如果该数据块读完，已经产生了一个整体的结果，则把这个结果写入到 ret
     * @return 下一个读取的数据块
     */
    @SuppressWarnings("all")
    Class<? extends Block> next(Map<Class<?>, Block<?>> blockAlreadyDecode, List ret);
}
