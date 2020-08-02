package com.sukaiyi.seaweedfsexplorer;

import com.sukaiyi.seaweedfsexplorer.nettyserver.ServerBootstrapConfig;

import java.util.Arrays;
import java.util.List;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class Application {

    /**
     * 该 static 块引用一些运行时需要的 Class， 以避免他们在打包时被 maven-shade-plugin 删除（配置了minimizeJar=true）
     */
    static {
        List<Class<?>> classesShouldNotBeRemoved = Arrays.asList(
                org.apache.log4j.ConsoleAppender.class,
                org.apache.log4j.PatternLayout.class
        );
        System.out.println("Class that should not be remove by maven-shade-plugin:");
        classesShouldNotBeRemoved.stream()
                .map(Class::getName)
                .forEach(System.out::println);
    }

    public static void main(String[] args) {

        System.getProperties().setProperty("runtime.args", String.join(",", args));

        new ServerBootstrapConfig().httpServerBootstrap();
    }

}
