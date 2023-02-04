package pxf.tl.io.virtual;

/**
 * an abstract vfs dir
 *
 * @author potatoxf
 */
public interface VirtualDirectory {
    /**
     * @return
     */
    String getPath();

    /**
     * @return
     */
    Iterable<VirtualFile> getFiles();

    /**
     *
     */
    default void close() {
    }
}
