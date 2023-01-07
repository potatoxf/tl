package pxf.tl.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author potatoxf
 */
public class FunctionException extends RuntimeException {
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionException.class);

    /**
     * Constructs a new runtime exception with the specified cause and a detail message of
     * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail
     * message of <tt>cause</tt>). This constructor is useful for runtime exceptions that are little
     * more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     */
    public FunctionException(Throwable cause) {
        super("Function Exception Caused By Inner Exception", cause);
        LOGGER.error("Function Exception", cause);
    }
}
