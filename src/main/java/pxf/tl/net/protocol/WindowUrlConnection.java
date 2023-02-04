package pxf.tl.net.protocol;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Window驱动盘URL
 *
 * @author potatoxf
 */
public class WindowUrlConnection extends URLConnection {

    public WindowUrlConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(url.toString());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(url.toString());
    }
}
