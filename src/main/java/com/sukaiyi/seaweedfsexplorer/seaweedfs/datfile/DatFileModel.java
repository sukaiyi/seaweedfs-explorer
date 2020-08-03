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

    public boolean isCompressed() {
        return (flags & 0x01) > 0;
    }

    public boolean hasName() {
        return (flags & 0x02) > 0;
    }

    public boolean hasMime() {
        return (flags & 0x04) > 0;
    }

    public boolean hasLastModifiedDate() {
        return (flags & 0x08) > 0;
    }

    public boolean hasTtl() {
        return (flags & 0x10) > 0;
    }

    public boolean hasPairs() {
        return (flags & 0x20) > 0;
    }

    public boolean isChunkManifest() {
        return (flags & 0x80) > 0;
    }

}
