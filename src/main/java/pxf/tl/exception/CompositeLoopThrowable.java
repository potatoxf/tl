package pxf.tl.exception;

import pxf.tl.help.Whether;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code CompositeLoopThrowable}用于在循环出现的异常{@link #addCause(Throwable)}和{@link #skip()}
 *
 * @author potatoxf
 */
public class CompositeLoopThrowable extends Throwable {
    private static final String LOOP_CAUSED_BY = "Loop caused by: ";
    private static final String SPLIT_LINE =
            "-------------------------------------------------------";
    private volatile List<Throwable> causes = null;

    /**
     * Constructs a new throwable with {@code null} as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}.
     *
     * <p>The {@link #fillInStackTrace()} method is called to initialize the stack trace data in the
     * newly created throwable.
     */
    public CompositeLoopThrowable() {
    }

    /**
     * Constructs a new throwable with the specified detail message. The cause is not initialized, and
     * may subsequently be initialized by a call to {@link #initCause}.
     *
     * <p>The {@link #fillInStackTrace()} method is called to initialize the stack trace data in the
     * newly created throwable.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the
     *                {@link #getMessage()} method.
     */
    public CompositeLoopThrowable(String message) {
        super(message);
    }

    /**
     * Constructs a new throwable with the specified detail message and cause.
     *
     * <p>Note that the detail message associated with {@code cause} is <i>not</i> automatically
     * incorporated in this throwable's detail message.
     *
     * <p>The {@link #fillInStackTrace()} method is called to initialize the stack trace data in the
     * newly created throwable.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link
     *                #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                (A {@code null} value is permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public CompositeLoopThrowable(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new throwable with the specified cause and a detail message of {@code (cause==null
     * ? null : cause.toString())} (which typically contains the class and detail message of {@code
     * cause}). This constructor is useful for throwables that are little more than wrappers for other
     * throwables (for example, {@link PrivilegedActionException}).
     *
     * <p>The {@link #fillInStackTrace()} method is called to initialize the stack trace data in the
     * newly created throwable.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     */
    public CompositeLoopThrowable(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new throwable with the specified detail message, cause, {@linkplain #addSuppressed
     * suppression} enabled or disabled, and writable stack trace enabled or disabled. If suppression
     * is disabled, {@link #getSuppressed} for this object will return a zero-length array and calls
     * to {@link #addSuppressed} that would otherwise append an exception to the suppressed list will
     * have no effect. If the writable stack trace is false, this constructor will not call {@link
     * #fillInStackTrace()}, a {@code null} will be written to the {@code stackTrace} field, and
     * subsequent calls to {@code fillInStackTrace} and {@link #setStackTrace(StackTraceElement[])}
     * will not set the stack trace. If the writable stack trace is false, {@link #getStackTrace} will
     * return a zero length array.
     *
     * <p>Note that the other constructors of {@code Throwable} treat suppression as being enabled and
     * the stack trace as being writable. Subclasses of {@code Throwable} should document any
     * conditions under which suppression is disabled and document conditions under which the stack
     * trace is not writable. Disabling of suppression should only occur in exceptional circumstances
     * where special requirements exist, such as a virtual machine reusing exception objects under
     * low-memory situations. Circumstances where a given exception object is repeatedly caught and
     * rethrown, such as to implement control flow between two sub-systems, is another situation where
     * immutable throwable objects would be appropriate.
     *
     * @param message            the detail message.
     * @param cause              the cause. (A {@code null} value is permitted, and indicates that the cause is
     *                           nonexistent or unknown.)
     * @param enableSuppression  whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     * @see OutOfMemoryError
     * @see NullPointerException
     * @see ArithmeticException
     */
    public CompositeLoopThrowable(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     *
     */
    public synchronized void skip() {
        if (causes == null) {
            causes = new ArrayList<>();
        }
        causes.add(null);
    }

    /**
     * @param cause
     */
    public synchronized void addCause(Throwable cause) {
        if (causes == null) {
            causes = new ArrayList<>();
        }
        if (cause != null) {
            causes.add(cause);
        }
    }

    /**
     * Prints this throwable and its backtrace to the specified print stream.
     *
     * @param s {@code PrintStream} to use for output
     */
    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        appendPrintStackTrace(new WrappedPrintStream(s));
    }

    /**
     * Prints this throwable and its backtrace to the specified print writer.
     *
     * @param s {@code PrintWriter} to use for output
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        appendPrintStackTrace(new WrappedPrintWriter(s));
    }

    private void appendPrintStackTrace(PrintStreamOrWriter s) {
        if (Whether.empty(causes)) {
            return;
        }
        int size = causes.size();
        synchronized (s.lock()) {
            s.print(LOOP_CAUSED_BY);
            s.println("{");
            for (int i = 0; i < size; i++) {
                Throwable cause = causes.get(i);
                if (cause == null) {
                    continue;
                }
                s.print("[");
                s.print(i);
                s.print("] ");
                if (s instanceof WrappedPrintStream) {
                    cause.printStackTrace(((WrappedPrintStream) s).printStream);
                } else {
                    cause.printStackTrace(((WrappedPrintWriter) s).printWriter);
                }
                if (i != size - 1) {
                    s.println(SPLIT_LINE);
                }
            }
            s.println("}");
        }
    }

    /**
     * Wrapper class for PrintStream and PrintWriter to enable a single implementation of
     * printStackTrace.
     */
    private abstract static class PrintStreamOrWriter {
        /**
         * Returns the object to be locked when using this StreamOrWriter
         */
        abstract Object lock();

        /**
         * Prints the specified string as a line on this StreamOrWriter
         */
        abstract void println(Object o);

        /**
         * Prints the specified string as a line on this StreamOrWriter
         */
        abstract void print(Object o);
    }

    private static class WrappedPrintStream extends PrintStreamOrWriter {
        private final PrintStream printStream;

        WrappedPrintStream(PrintStream printStream) {
            this.printStream = printStream;
        }

        Object lock() {
            return printStream;
        }

        void println(Object o) {
            printStream.println(o);
        }

        void print(Object o) {
            printStream.print(o);
        }
    }

    private static class WrappedPrintWriter extends PrintStreamOrWriter {
        private final PrintWriter printWriter;

        WrappedPrintWriter(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        Object lock() {
            return printWriter;
        }

        void println(Object o) {
            printWriter.println(o);
        }

        void print(Object o) {
            printWriter.print(o);
        }
    }
}
