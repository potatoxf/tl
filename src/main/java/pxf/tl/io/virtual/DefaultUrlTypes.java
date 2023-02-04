package pxf.tl.io.virtual;


import pxf.tl.util.ToolBytecode;

import java.net.JarURLConnection;
import java.net.URL;

/**
 * default url types used by {@link Vfs#fromURL(URL)}
 *
 * <p>
 *
 * <p>jarFile - creates a {@link ZipVirtualDirectory} over jar file
 *
 * <p>jarUrl - creates a {@link ZipVirtualDirectory} over a jar url, using Java's {@link
 * JarURLConnection}
 *
 * <p>directory - creates a {@link SystemVirtualDirectory} over a file system directory
 *
 * <p>jboss vfs - for protocols vfs, using jboss vfs (should be provided in classpath)
 *
 * <p>jboss vfsfile - creates a {@link DefaultUrlType.UrlTypeVFS} for protocols vfszip and vfsfile.
 *
 * <p>bundle - for bundle protocol, using eclipse FileLocator (should be provided in classpath)
 *
 * <p>jarInputStream - creates a {@link JarInputVirtualDirectory} over jar files (contains ".jar!/"
 * in it's name), using Java's JarInputStream
 */
public enum DefaultUrlTypes implements UrlType {
    bundle {
        public boolean matches(URL url) throws Exception {
            return url.getProtocol().startsWith("bundle");
        }

        public VirtualDirectory createDir(URL url) throws Exception {
            return Vfs.fromURL(
                    (URL)
                            ToolBytecode.getClassLoader()
                                    .loadClass("org.eclipse.core.runtime.FileLocator")
                                    .getMethod("resolve", URL.class)
                                    .invoke(null, url));
        }
    },
}
