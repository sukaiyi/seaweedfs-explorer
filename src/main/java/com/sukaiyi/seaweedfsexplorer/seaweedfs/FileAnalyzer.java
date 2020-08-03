package com.sukaiyi.seaweedfsexplorer.seaweedfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author sukaiyi
 * @date 2020/07/30
 */
public interface FileAnalyzer<T> {
    default List<T> exec(File file) {
        Objects.requireNonNull(file);
        List<T> ts = new ArrayList<>();

        try (InputStream os = new FileInputStream(file)) {
            byte[] buffer = new byte[1024 * 256];
            int count;
            while ((count = os.read(buffer)) > 0) {
                decode(buffer, count, ts);
            }
            decode(buffer, count, ts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ts;
    }

    default List<T> exec(String file) {
        Objects.requireNonNull(file);
        return exec(new File(file));
    }

    void decode(byte[] bytes, int count, List<T> result);
}
