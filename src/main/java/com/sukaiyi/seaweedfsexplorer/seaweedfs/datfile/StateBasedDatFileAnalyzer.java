package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.StateBasedFileAnalyzer;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile.blocks.SuperBlock;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class StateBasedDatFileAnalyzer extends StateBasedFileAnalyzer<DatFileModel> {
    @Override
    @SuppressWarnings("all")
    protected Class<? extends Block> initState() {
        return SuperBlock.class;
    }
}
