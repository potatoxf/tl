package pxf.tl.exception;

/**
 * 不支持异常
 *
 * @author potatoxf
 */
public class UnsupportedException extends RuntimeException {

    private static final String PREFIX = "Unsupported: ";

    /**
     * Constructs a new runtime exception with the specified detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public UnsupportedException(String message) {
        super(PREFIX + message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically
     * incorporated in this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link
     *                #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                (A {@code null} value is permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public UnsupportedException(String message, Throwable cause) {
        super(PREFIX + message, cause);
    }
}
