package com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile;

import com.sukaiyi.seaweedfsexplorer.utils.ByteUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IdxFileAnalyzerTest {

    @Test
    void decode() {
        IdxFileAnalyzer analyzer = new IdxFileAnalyzer();
        List<IdxFileModel> modelList = analyzer.exec("C:\\Users\\sukaiyi\\Downloads\\weed\\5.idx");
        System.out.println(modelList.size());
        assertNotNull(modelList);
    }

    @Test
    void toHexString() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("C:\\Users\\sukaiyi\\Downloads\\weed\\5.idx"));
        String hex = ByteUtils.byteToHexString(bytes, 0, bytes.length);
        System.out.println(hex);
    }
}