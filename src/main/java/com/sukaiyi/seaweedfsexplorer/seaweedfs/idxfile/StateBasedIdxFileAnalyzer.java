package com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile;

import com.sukaiyi.seaweedfsexplorer.seaweedfs.Block;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.StateBasedFileAnalyzer;
import com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile.blocks.IdBlock;

/**
 * @author sukaiyi
 * @date 2020/08/03
 */
public class StateBasedIdxFileAnalyzer extends StateBasedFileAnalyzer<IdxFileModel> {
    @Override
    @SuppressWarnings("all")
    protected Class<? extends Block> initState() {
        return IdBlock.class;
    }
}
