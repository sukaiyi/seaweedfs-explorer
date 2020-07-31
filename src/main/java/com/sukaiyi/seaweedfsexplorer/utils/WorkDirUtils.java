package com.sukaiyi.seaweedfsexplorer.utils;

import java.io.File;
import java.util.Properties;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class WorkDirUtils {

    private WorkDirUtils() {

    }

    public static String getWorkPath() {
        Properties properties = System.getProperties();
        String workPath = properties.getProperty("runtime.args");
        if (workPath == null || workPath.isEmpty()) {
            workPath = properties.getProperty("user.dir");
        }
        return workPath;
    }

    public static File getWorkDir() {
        return new File(getWorkPath());
    }

}
