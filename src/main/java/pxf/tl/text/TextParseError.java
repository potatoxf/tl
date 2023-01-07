package pxf.tl.text;

/**
 * Represents an error or a warning which occurred when parsing an input.
 *
 * <p>Used by {@link TextParseException} to collect as many errors as possible before failing
 * (throwing).
 *
 * @author potatoxf
 */
public class TextParseError {

    private final Severity severity;
    private final TextPosition pos;
    private final String message;

    public TextParseError(TextPosition pos, String message, Severity severity) {
        this.pos = pos;
        this.message = message;
        this.severity = severity;
    }

    /**
     * Creates a new warning for the given position with the given message.
     *
     * <p>If no position is available {@link TextPosition#UNKNOWN} can be used
     *
     * @param pos the position where the warning occurred
     * @param msg the message explaining the warning
     * @return a new TextParseError containing the warning
     */
    public static TextParseError warning(TextPosition pos, String msg) {
        String message = msg;
        if (pos.atLine() > 0) {
            message = String.format("%d:%d: %s", pos.atLine(), pos.atPosition(), msg);
        }
        return new TextParseError(pos, message, Severity.WARNING);
    }

    /**
     * Creates a new error for the given position with the given message.
     *
     * <p>If no position is available {@link TextPosition#UNKNOWN} can be used
     *
     * @param pos the position where the error occurred
     * @param msg the message explaining the error
     * @return a new TextParseError containing the error
     */
    public static TextParseError error(TextPosition pos, String msg) {
        String message = msg;
        if (pos.atLine() > 0) {
            message = String.format("%3d:%2d: %s", pos.atLine(), pos.atPosition(), msg);
        }
        return new TextParseError(pos, message, Severity.ERROR);
    }

    /**
     * Provides the position where the error or warning occurred.
     *
     * @return the position of this error or warning
     */
    public TextPosition getPosition() {
        return pos;
    }

    /**
     * Provides the message explaining the error or warning.
     *
     * @return the message of this error or warning
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the textParseSeverity, which indicates if this is an error or a warning.
     *
     * @return the textParseSeverity of this.
     */
    public Severity getTextParseSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return String.format("%s %s", severity, message);
    }

    /**
     * Specifies whether an error (unrecoverable problem) or a warning occurred.
     *
     * @author potatoxf
     */
    public enum Severity {
        /**
         * 警告
         */
        WARNING,
        /**
         * 错误
         */
        ERROR
    }
}
