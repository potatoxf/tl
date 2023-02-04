package pxf.tl.exception;

import java.io.IOException;

/**
 * IO异常细节
 *
 * @author potatoxf
 */
public class IODetailException extends IORuntimeException {

    /**
     * The path
     */
    private String srcPath;
    /**
     * The path
     */
    private String destPath;

    public IODetailException(String message) {
        super(message);
    }

    public IODetailException(String message, String path) {
        this(message, path, null);
    }

    public IODetailException(String message, IOException cause, String path) {
        this(message, cause, path, null);
    }

    public IODetailException(String message, String srcPath, String destPath) {
        super(message);
        this.srcPath = srcPath;
        this.destPath = destPath;
    }

    public IODetailException(String message, IOException cause, String srcPath, String destPath) {
        super(message, cause);
        this.srcPath = srcPath;
        this.destPath = destPath;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public String getDestPath() {
        return destPath;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance (which may be {@code
     * null}).
     */
    @Override
    public String getMessage() {

        if (srcPath != null) {
            return super.getMessage() + "[" + srcPath + "]";
        } else {
            return super.getMessage();
        }
    }
}
