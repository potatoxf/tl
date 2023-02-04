package pxf.tl.text;

import java.util.List;

/**
 * Used to signal that processing an input failed.
 *
 * <p>By first collecting as many {@link TextParseError} instances as possible, this permits to
 * provide good insights of what is wrong with the input provided by the user.
 *
 * @author potatoxf
 */
public class TextParseException extends RuntimeException {

    private static final long serialVersionUID = -5618855459424320517L;

    private final transient List<TextParseError> errors;

    private TextParseException(String message, List<TextParseError> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Creates a new exception based on the list of errors.
     *
     * @param errors the errors which occurred while processing the user input
     * @return a new TextParseException which can be thrown
     */
    public static TextParseException create(List<TextParseError> errors) {
        if (errors.size() == 1) {
            return new TextParseException(errors.get(0).getMessage(), errors);
        } else if (errors.size() > 1) {
            return new TextParseException(
                    String.format("%d errors occured. First: %s", errors.size(), errors.get(0).getMessage()),
                    errors);
        } else {
            return new TextParseException("An unknown error occured", errors);
        }
    }

    /**
     * Provides a list of all errors and warnings which occurred
     *
     * @return all errors and warnings which occurred while processing the user input
     */
    public List<TextParseError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TextParseError error : errors) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(error);
        }

        return sb.toString();
    }
}
