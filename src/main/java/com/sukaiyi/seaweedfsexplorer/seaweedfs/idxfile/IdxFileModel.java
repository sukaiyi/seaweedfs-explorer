package com.sukaiyi.seaweedfsexplorer.seaweedfs.idxfile;


import lombok.Data;

/**
 * @author sukaiyi
 * @date 2020/07/30
 */
@Data
public class IdxFileModel {
    private String id;
    private long offset;
    private long size;
}
