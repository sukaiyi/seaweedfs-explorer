package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatFileAnalyzerTest {

    @Test
    void decode() {
        DatFileAnalyzer analyzer = new DatFileAnalyzer();
        List<DatFileModel> modelList = analyzer.exec("C:\\Users\\sukaiyi\\Downloads\\weed\\24.dat");
        System.out.println(modelList.size());
        assertNotNull(modelList);
    }
}