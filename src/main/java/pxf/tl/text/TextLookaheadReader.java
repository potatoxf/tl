package pxf.tl.text;


import pxf.tl.help.New;
import pxf.tl.help.Whether;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * An efficient reader of character streams, reading character by character and supporting
 * lookahead.
 *
 * <p>Helps to read characters from a {@link Reader} one after another. Using <tt>next</tt>,
 * upcoming characters can be inspected without consuming (removing) the current one.
 *
 * @author potatoxf
 */
public class TextLookaheadReader extends TextLookahead<TextChar> {
    private final Reader input;
    private int line = 1;
    private int pos = 0;

    /**
     * Creates a new TextLookaheadReader for the given Reader.
     *
     * <p>Internally a {@link BufferedReader} is used to efficiently read single characters. The given
     * reader will not be closed by this class.
     *
     * @param input      the reader to draw the input from
     * @param endOfInput The end symbol
     */
    public TextLookaheadReader(Reader input, char endOfInput) {
        super(endOfInput);
        if (input == null) {
            throw new IllegalArgumentException("The input must not be null");
        }
        this.input = New.bufferedReader(input);
    }

    @Override
    protected TextChar endOfInput() {
        return new TextCharImpl(endOfInput, line, pos);
    }

    @Override
    protected TextChar fetch() {
        int character;
        try {
            character = input.read();
        } catch (IOException e) {
            problemCollector.add(
                    TextParseError.error(new TextCharImpl(endOfInput, line, pos), e.getMessage()));
            return null;
        }
        if (character == -1) {
            return null;
        }
        TextChar result = new TextCharImpl((char) character, line, ++pos);
        if (character == '\n') {
            line++;
            pos = 0;
        }
        return result;
    }

    @Override
    public String toString() {
        if (Whether.empty(itemBuffer)) {
            return line + ":" + pos + ": Buffer empty";
        }
        if (itemBuffer.size() < 2) {
            return line + ":" + pos + ": " + current();
        }
        return line + ":" + pos + ": " + current() + ", " + next();
    }

    private class TextCharImpl extends TextChar {

        public TextCharImpl(char value, int line, int pos) {
            super(value, line, pos);
        }

        /**
         * Determines if this instance represents the end of input indicator
         *
         * @return <tt>true</tt> if this instance represents the end of the underlying input,
         * <tt>false</tt> otherwise
         */
        @Override
        public boolean isEndOfInput() {
            return value == endOfInput;
        }
    }
}
