package com.sukaiyi.seaweedfsexplorer;

import com.sukaiyi.seaweedfsexplorer.nettyserver.ServerBootstrapConfig;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class Application {

    public static void main(String[] args) {

        System.getProperties().setProperty("runtime.args", String.join(",", args));

        new ServerBootstrapConfig().httpServerBootstrap();
    }

}
