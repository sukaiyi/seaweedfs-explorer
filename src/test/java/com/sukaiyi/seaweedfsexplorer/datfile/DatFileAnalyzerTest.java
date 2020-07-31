package com.sukaiyi.seaweedfsexplorer.datfile;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.DatFileAnalyzer;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.DatFileModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatFileAnalyzerTest {

    @Test
    void decode() {
        DatFileAnalyzer analyzer = new DatFileAnalyzer();
        List<DatFileModel> modelList = analyzer.exec("C:\\Users\\HT-Dev\\Downloads\\weed\\data-ins0\\5.dat");
        System.out.println(modelList.size());
        assertNotNull(modelList);
    }
}