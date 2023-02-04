package pxf.tl.text;

import java.io.Serializable;

/**
 * 文本位置
 *
 * @author potatoxf
 */
public class TextPosition implements Serializable {

    /**
     * 未知
     */
    public static final TextPosition UNKNOWN = new TextPosition(0, 0);

    /**
     * 行号
     */
    protected int line;

    /**
     * 行中位置
     */
    protected int position;

    public TextPosition() {
        this(0, 0);
    }

    public TextPosition(int line, int position) {
        this.line = line;
        this.position = position;
    }

    public int atLine() {
        return line;
    }

    public int atPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "(" + line + "," + position + ")";
    }
}
