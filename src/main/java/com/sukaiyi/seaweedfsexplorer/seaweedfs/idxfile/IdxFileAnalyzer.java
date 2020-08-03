package com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.FileAnalyzer;
import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;
import com.sukaiyi.seaweedfsexplorer.utils.FileIdFormatUtils;

import java.util.List;

import static com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.IdxFileAnalyzer.State.*;

/**
 * @author sukaiyi
 * @date 2020/08/01
 */
@Deprecated
public class IdxFileAnalyzer implements FileAnalyzer<IdxFileModel> {

    private static final int ID_SIZE = 8;
    private static final int OFFSET_SIZE = 4;
    private static final int SIZE_SIZE = 4;

    private static final int NEEDLE_PADDING_SIZE = 8;

    private final ThreadLocal<AnalyzeState> analyzeStateLocal = ThreadLocal.withInitial(AnalyzeState::new);

    @Override
    public void decode(byte[] bytes, int count, List<IdxFileModel> result) {
        AnalyzeState analyzeState = analyzeStateLocal.get();
        for (int i = 0; i < count; ) {
            switch (analyzeState.state) {
                case ID:
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= ID_SIZE) {
                        String id = ByteUtils.byteToHexString(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data = new IdxFileModel();
                        analyzeState.data.setId(FileIdFormatUtils.formatId(id));
                        analyzeState.state = OFFSET;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case OFFSET:
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= OFFSET_SIZE) {
                        long offset = ByteUtils.byteToUnsignedLong(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setOffset(offset * NEEDLE_PADDING_SIZE);
                        analyzeState.state = SIZE;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case SIZE:
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= SIZE_SIZE) {
                        long size = ByteUtils.byteToUnsignedLong(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setSize(size);
                        analyzeState.state = ID;
                        analyzeState.bufPos = 0;
                        // 等于0的文件是删除的
                        if (analyzeState.data.getOffset() != 0 && analyzeState.data.getSize() != 0) {
                            result.add(analyzeState.data);
                        }
                        analyzeState.data = null;
                    }
                    break;
            }
        }
    }

    private static final class AnalyzeState {
        private State state = ID;
        private IdxFileModel data;
        private final byte[] buff = new byte[8]; // ID最长，8个字节
        private Integer bufPos = 0;
    }

    enum State {
        ID,
        OFFSET,
        SIZE
    }
}
