package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.IdxFileAnalyzer;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.IdxFileModel;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.StateBasedIdxFileAnalyzer;
import com.sukaiyi.seaweedfsexplorer.utils.ReflectUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateBasedFileAnalyzerTest {

    @Test
    void testDatFile() {
        StateBasedDatFileAnalyzer stateBasedDatFileAnalyzer = new StateBasedDatFileAnalyzer();
        List<DatFileModel> stateBasedResult = stateBasedDatFileAnalyzer.exec("C:\\Users\\HT-Dev\\Downloads\\weed\\data-ins1\\5.dat");

        DatFileAnalyzer normalAnalyzer = new DatFileAnalyzer();
        List<DatFileModel> normalResult = normalAnalyzer.exec("C:\\Users\\HT-Dev\\Downloads\\weed\\data-ins1\\5.dat");

        assertEquals(stateBasedResult.size(), normalResult.size());

        Field[] fields = DatFileModel.class.getDeclaredFields();
        for (int i = 0; i < stateBasedResult.size(); i++) {
            DatFileModel stateBased = stateBasedResult.get(i);
            DatFileModel normal = normalResult.get(i);
            for (Field field : fields) {
                Object stateBasedValue = ReflectUtils.getFieldValue(stateBased, field);
                Object normalValue = ReflectUtils.getFieldValue(normal, field);
                assertEquals(stateBasedValue, normalValue, field.getName());
            }
        }
    }

    @Test
    void testIdxFile() {
        StateBasedIdxFileAnalyzer stateBasedIdxFileAnalyzer = new StateBasedIdxFileAnalyzer();
        List<IdxFileModel> stateBasedResult = stateBasedIdxFileAnalyzer.exec("C:\\Users\\HT-Dev\\Downloads\\weed\\data-ins1\\5.idx");

        IdxFileAnalyzer normalAnalyzer = new IdxFileAnalyzer();
        List<IdxFileModel> normalResult = normalAnalyzer.exec("C:\\Users\\HT-Dev\\Downloads\\weed\\data-ins1\\5.idx");

        assertEquals(stateBasedResult.size(), normalResult.size());

        Field[] fields = IdxFileModel.class.getDeclaredFields();
        for (int i = 0; i < stateBasedResult.size(); i++) {
            IdxFileModel stateBased = stateBasedResult.get(i);
            IdxFileModel normal = normalResult.get(i);
            for (Field field : fields) {
                Object stateBasedValue = ReflectUtils.getFieldValue(stateBased, field);
                Object normalValue = ReflectUtils.getFieldValue(normal, field);
                assertEquals(stateBasedValue, normalValue, field.getName());
            }
        }
    }
}