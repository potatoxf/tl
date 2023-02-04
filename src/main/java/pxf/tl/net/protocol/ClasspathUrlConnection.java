package pxf.tl.net.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 类路径URL连接
 *
 * @author potatoxf
 */
public class ClasspathUrlConnection extends URLConnection {

    public ClasspathUrlConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public InputStream getInputStream() throws IOException {
        String classpath = url.toString().substring(10);
        if (classpath.startsWith("/")) {
            classpath = classpath.substring(1);
        }
        return ClasspathUrlConnection.class.getResourceAsStream(classpath);
    }
}
