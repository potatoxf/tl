package pxf.tl.text;


import pxf.tl.lang.AbstractLookahead;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract data structure for providing streams of items with a lookahead.
 *
 * <p>Items provided by subclasses of this are processed one after each other. However, using {@link
 * #next()} or {@link #next(int)} one can peek at upcoming items without consuming the current one.
 *
 * @param <T> the type of the TextPosition kept in the stream
 * @author potatoxf
 */
public abstract class AbstractTextLookahead<T extends TextPosition> extends AbstractLookahead<T> {

    /**
     * The end symbol
     */
    protected final char endOfInput;

    /**
     * Used to collect problems which occurred when processing the input. This is used instead of
     * classic exceptions, so that errors can be recovered and we can continue to process to input to
     * check for further errors or problems.
     */
    protected List<TextParseError> problemCollector = new ArrayList<TextParseError>();

    protected AbstractTextLookahead() {
        this('\0');
    }

    protected AbstractTextLookahead(char endOfInput) {
        this.endOfInput = endOfInput;
    }

    /**
     * Provides access to the problem collector used by this instance.
     *
     * @return the problem collector used by this class. This returns the internally used list,
     * therefore it should be treat appropriately
     */
    public List<TextParseError> getProblemCollector() {
        return problemCollector;
    }

    /**
     * Installs the given problem collector.
     *
     * @param problemCollector the problem collector to be from now on
     */
    public void setProblemCollector(List<TextParseError> problemCollector) {
        this.problemCollector = problemCollector;
    }
}
