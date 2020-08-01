package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.FileAnalyzer;
import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;
import com.sukaiyi.seaweedfsexplorer.utils.FileIdFormatUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.DatFileAnalyzer.State.*;

/**
 * @author sukaiyi
 * @date 2020/07/30
 */
public class DatFileAnalyzer implements FileAnalyzer<DatFileModel> {

    public static final int SUPER_BLOCK_SIZE = 8;
    public static final int COOKIE_SIZE = 8;
    public static final int ID_SIZE = 4;
    public static final int TOTAL_SIZE_SIZE = 4;
    public static final int DATA_SIZE_SIZE = 4;
    public static final int FLAG_SIZE = 1;
    public static final int NAME_SIZE_SIZE = 1;
    public static final int MIME_SIZE_SIZE = 1;
    public static final int PAIRS_SIZE_SIZE = 2;
    public static final int LAST_MODIFIED_SIZE = 5;
    public static final int TTL_SIZE = 2;
    public static final int CHECK_SUM_SIZE = 4;
    public static final int APPEND_AT_NS_SIZE = 8;

    private final ThreadLocal<AnalyzeState> analyzeStateLocal = ThreadLocal.withInitial(AnalyzeState::new);
    private final ThreadLocal<Integer> versionLocal = ThreadLocal.withInitial(() -> 3); // 从 SUPER_BLOCK 中读取版本信息

    @Override
    public void decode(byte[] bytes, int count, List<DatFileModel> result) {
        AnalyzeState analyzeState = analyzeStateLocal.get();
        for (int i = 0; i < count; ) {
            switch (analyzeState.state) {
                case SUPER_BLOCK:
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= SUPER_BLOCK_SIZE) {
                        versionLocal.set(Byte.toUnsignedInt(analyzeState.buff[0]));
                        analyzeState.state = COOKIE;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case COOKIE:
                    if (analyzeState.data == null) {
                        analyzeState.data = new DatFileModel();
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= COOKIE_SIZE) {
                        String cookie = ByteUtils.byteToHexString(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setCookie(FileIdFormatUtils.format(cookie));
                        analyzeState.state = ID;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case ID:
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= ID_SIZE) {
                        String id = ByteUtils.byteToHexString(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setId(FileIdFormatUtils.format(id));
                        analyzeState.state = TOTAL_SIZE;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case TOTAL_SIZE:
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= TOTAL_SIZE_SIZE) {
                        long totalSize = ByteUtils.byteToUnsignedLong(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setSize(totalSize);
                        analyzeState.state = totalSize > 0 ? DATA_SIZE : CHECK_SUM;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case DATA_SIZE:
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= DATA_SIZE_SIZE) {
                        long dataSize = ByteUtils.byteToUnsignedLong(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setDataSize(dataSize);
                        analyzeState.state = DATA;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case DATA:
                    long step = Math.min(bytes.length - i, analyzeState.data.getDataSize() - analyzeState.bufPos);
                    analyzeState.bufPos += (int) step;
                    i += step;

                    if (analyzeState.bufPos >= analyzeState.data.getDataSize()) {
                        analyzeState.state = FLAGS;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case FLAGS:
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= FLAG_SIZE) {
                        analyzeState.data.setFlags(analyzeState.buff[0]);
                        analyzeState.state = NAME_SIZE;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case NAME_SIZE:
                    if ((analyzeState.data.getFlags() & 0x02) == 0) { // FlagHasName
                        analyzeState.state = MIME_SIZE;
                        analyzeState.bufPos = 0;
                        break;
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= NAME_SIZE_SIZE) {
                        long nameSize = ByteUtils.byteToUnsignedLong(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setNameSize((int) nameSize);
                        analyzeState.state = NAME;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case NAME:
                    if (analyzeState.data.getNameSize() <= 0) {
                        analyzeState.state = MIME_SIZE;
                        break;
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= analyzeState.data.getNameSize()) {
                        String name = new String(analyzeState.buff, 0, analyzeState.bufPos, StandardCharsets.UTF_8);
                        analyzeState.data.setName(name);
                        analyzeState.state = MIME_SIZE;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case MIME_SIZE:
                    if ((analyzeState.data.getFlags() & 0x04) == 0) { // FlagHasMime
                        analyzeState.state = PAIRS_SIZE;
                        analyzeState.bufPos = 0;
                        break;
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= MIME_SIZE_SIZE) {
                        long mimeSize = ByteUtils.byteToUnsignedLong(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setMimeSize((int) mimeSize);
                        analyzeState.state = MIME;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case MIME:
                    if (analyzeState.data.getMimeSize() <= 0) {
                        analyzeState.state = PAIRS_SIZE;
                        break;
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= analyzeState.data.getMimeSize()) {
                        String mime = new String(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setMime(mime);
                        analyzeState.state = PAIRS_SIZE;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case PAIRS_SIZE:
                    if ((analyzeState.data.getFlags() & 0x20) == 0) { // FlagHasPairs
                        analyzeState.state = LAST_MODIFIED;
                        analyzeState.bufPos = 0;
                        break;
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= PAIRS_SIZE_SIZE) {
                        long pairsSize = ByteUtils.byteToUnsignedLong(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setPairsSize((int) pairsSize);
                        analyzeState.state = PAIRS;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case PAIRS:
                    if (analyzeState.data.getPairsSize() <= 0) {
                        analyzeState.state = LAST_MODIFIED;
                        break;
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= analyzeState.data.getPairsSize()) {
                        String pairs = new String(analyzeState.buff, 0, analyzeState.bufPos, StandardCharsets.UTF_8);
                        analyzeState.data.setPairs(pairs);
                        analyzeState.state = LAST_MODIFIED;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case LAST_MODIFIED:
                    if ((analyzeState.data.getFlags() & 0x08) == 0) { // FlagHasLastModifiedDate
                        analyzeState.state = TTL;
                        analyzeState.bufPos = 0;
                        break;
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= LAST_MODIFIED_SIZE) {
                        long lastModified = ByteUtils.byteToUnsignedLong(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setLastModified(lastModified);
                        analyzeState.state = TTL;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case TTL:
                    if ((analyzeState.data.getFlags() & 0x10) == 0) { // FlagHasTtl
                        analyzeState.state = CHECK_SUM;
                        analyzeState.bufPos = 0;
                        break;
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= TTL_SIZE) {
                        byte[] ttl = new byte[TTL_SIZE];
                        System.arraycopy(analyzeState.buff, 0, ttl, 0, TTL_SIZE);
                        analyzeState.data.setTtl(ttl);
                        analyzeState.state = CHECK_SUM;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case CHECK_SUM:
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= CHECK_SUM_SIZE) {
                        String checkSum = ByteUtils.byteToHexString(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setCheckSum(checkSum);
                        analyzeState.state = APPEND_AT_NS;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case APPEND_AT_NS: // 版本 3 开始才有该字段
                    if (versionLocal.get() < 3) {
                        analyzeState.state = PADDING;
                        break;
                    }
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= APPEND_AT_NS_SIZE) {
                        long appendAtNs = ByteUtils.byteToUnsignedLong(analyzeState.buff, 0, analyzeState.bufPos);
                        analyzeState.data.setAppendAtNs(appendAtNs);
                        analyzeState.state = PADDING;
                        analyzeState.readThisData += analyzeState.bufPos;
                        analyzeState.bufPos = 0;
                    }
                    break;
                case PADDING: // Aligned to 8 bytes
                    analyzeState.buff[analyzeState.bufPos++] = bytes[i++];
                    if (analyzeState.bufPos >= 8 - analyzeState.readThisData % 8) {
                        result.add(analyzeState.data);
                        analyzeState.data = null;
                        analyzeState.state = COOKIE;
                        analyzeState.readThisData = 0L;
                        analyzeState.bufPos = 0;
                    }
                    break;
            }
        }
    }

    private static final class AnalyzeState {
        private State state = SUPER_BLOCK;
        private DatFileModel data;
        private Long readThisData = 0L;
        private final byte[] buff = new byte[1024 * 64]; // Pairs maximum 64kB
        private Integer bufPos = 0;
    }

    enum State {
        SUPER_BLOCK,
        COOKIE,
        ID,
        TOTAL_SIZE,
        DATA_SIZE,
        DATA,
        FLAGS,
        NAME_SIZE,
        NAME,
        MIME_SIZE,
        MIME,
        PAIRS_SIZE,
        PAIRS,
        LAST_MODIFIED,
        TTL,
        CHECK_SUM,
        APPEND_AT_NS,
        PADDING
    }
}
