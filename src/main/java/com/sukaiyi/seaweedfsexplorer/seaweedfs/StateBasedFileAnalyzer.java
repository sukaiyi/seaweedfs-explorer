package com.sukaiyi.seaweedfsexplorer.seaweedfs;

import com.sukaiyi.seaweedfsexplorer.utils.MathUtils;
import com.sukaiyi.seaweedfsexplorer.utils.ReflectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sukaiyi
 * @date 2020/07/30
 */
public abstract class StateBasedFileAnalyzer<T> implements FileAnalyzer<T> {

    private final ThreadLocal<AnalyzeState> analyzeStateLocal = ThreadLocal.withInitial(() -> {
        AnalyzeState analyzeState = new AnalyzeState();
        analyzeState.state = initState();
        analyzeState.buff = new byte[1024];
        return analyzeState;
    });
    private final ThreadLocal<Map<Class<?>, Block<?>>> blockAlreadyDecodeLocal = ThreadLocal.withInitial(HashMap::new);

    @Override
    @SuppressWarnings("all")
    public void decode(byte[] bytes, int count, List<T> result) {
        AnalyzeState analyzeState = analyzeStateLocal.get();
        Map<Class<?>, Block<?>> blockAlreadyDecode = blockAlreadyDecodeLocal.get();
        for (int i = 0; i < count; ) {
            Class<? extends Block> state = analyzeState.state;
            Block block = blockAlreadyDecode.get(state);
            if (block == null) {
                block = ReflectUtils.newInstance(state);
                blockAlreadyDecode.put(state, block);
                analyzeState.size = block.size(blockAlreadyDecode);
                analyzeState.read = 0L;
                analyzeState.bufPos = 0;
            }
            long left = analyzeState.size - analyzeState.read;
            if (left > analyzeState.buff.length) { // 如果剩余未读字节数大于了缓冲区大小，就不拷贝到缓冲区
                int read = MathUtils.min((long) count - i, analyzeState.size - analyzeState.read).intValue();
                i += read;
                analyzeState.read += read;
                if (analyzeState.read - analyzeState.size == 0) { // 数读取完了
                    block.decode(blockAlreadyDecode, bytes, i - read, read, true);
                    analyzeState.state = block.next(blockAlreadyDecode, result);
                } else {
                    block.decode(blockAlreadyDecode, bytes, i - read, read, false);
                }
            } else { // 拷贝到缓冲区
                int read = MathUtils.min((long) count - i, (long) analyzeState.buff.length - analyzeState.bufPos, analyzeState.size - analyzeState.read).intValue();
                System.arraycopy(bytes, i, analyzeState.buff, analyzeState.bufPos, read);
                i += read;
                analyzeState.bufPos += read;
                analyzeState.read += read;

                if (analyzeState.read - analyzeState.size == 0) { // 数读取完了
                    block.decode(blockAlreadyDecode, analyzeState.buff, 0, analyzeState.bufPos, true);
                    analyzeState.bufPos = 0;
                    analyzeState.state = block.next(blockAlreadyDecode, result);
                } else if (analyzeState.buff.length - analyzeState.bufPos == 0) { // 缓冲区满了
                    block.decode(blockAlreadyDecode, analyzeState.buff, 0, analyzeState.bufPos, false);
                    analyzeState.bufPos = 0;
                }
            }
        }
    }

    @SuppressWarnings("all")
    protected abstract Class<? extends Block> initState();

    @SuppressWarnings("all")
    private static final class AnalyzeState {
        private Class<? extends Block> state;
        private byte[] buff;
        private Integer bufPos = 0;
        private Long read; // 已经读取的字节数
        private Long size; // 当前 Block 总共需要读取的字节数
    }

}
