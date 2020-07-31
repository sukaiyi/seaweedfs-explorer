package com.sukaiyi.seaweedfsexplorer.seaweedfs.datfile;


import lombok.Data;

/**
 * @author sukaiyi
 * @date 2020/07/30
 */
@Data
public class DatFileModel {
    private String cookie;
    private String id;
    private long size;
    private long dataSize;
    private byte[] data;
    private byte flags;
    private int nameSize;
    private String name;
    private int mimeSize;
    private String mime;
    private int pairsSize;
    private String pairs;
    private long lastModified;
    private byte[] ttl;
    private String checkSum;
    private long appendAtNs;
}
