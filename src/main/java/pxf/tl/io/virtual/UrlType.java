package pxf.tl.io.virtual;

import java.net.URL;

/**
 * a matcher and factory for a url
 *
 * @author potatoxf
 */
public interface UrlType {
    /**
     * @param url
     * @return
     * @throws Exception
     */
    boolean matches(URL url) throws Exception;

    /**
     * @param url
     * @return
     * @throws Exception
     */
    VirtualDirectory createDir(URL url) throws Exception;
}
