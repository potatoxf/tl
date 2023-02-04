package pxf.tl.net.protocol.g;


import pxf.tl.net.protocol.WindowUrlConnection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Window驱动盘URL
 *
 * @author potatoxf
 */
public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new WindowUrlConnection(u);
    }
}
