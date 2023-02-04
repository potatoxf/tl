package pxf.tl.io.virtual;

import java.io.IOException;
import java.io.InputStream;

/**
 * an abstract vfs file
 *
 * @author potatoxf
 */
public interface VirtualFile {
    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    String getRelativePath();

    /**
     * @return
     * @throws IOException
     */
    InputStream openInputStream() throws IOException;
}
