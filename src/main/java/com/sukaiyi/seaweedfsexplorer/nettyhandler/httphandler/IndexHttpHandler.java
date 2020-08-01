package com.sukaiyi.seaweedfsexplorer.nettyhandler.httphandler;

/**
 * @author sukaiyi
 * @date 2020/07/31
 */
public class IndexHttpHandler extends AbstractStaticHttpHandler {

    @Override
    protected String resource() {
        return "pages/index.html";
    }

    @Override
    protected boolean cacheable() {
        return true;
    }
}
