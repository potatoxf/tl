package pxf.tl.iter;

import pxf.tl.api.Charsets;
import pxf.tl.help.New;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

/**
 * 行迭代器
 *
 * @author potatoxf
 */
public class LineIter extends AbstractIter<String> implements Iterator<String>, Iterable<String>, AutoCloseable {

    /**
     *
     */
    private final BufferedReader bufferedReader;
    /**
     *
     */
    private final boolean isAutoClose;

    /**
     * @param inputStream
     */
    public LineIter(InputStream inputStream, Charsets charsets) {
        this(New.bufferedReader(inputStream, charsets));
    }

    /**
     * @param inputStream
     * @param isAutoClose
     */
    public LineIter(InputStream inputStream, Charsets charsets, boolean isAutoClose) {
        this(New.bufferedReader(inputStream, charsets), isAutoClose);
    }

    /**
     * @param reader
     */
    public LineIter(Reader reader) {
        this(reader, true);
    }

    /**
     * @param reader
     * @param isAutoClose
     */
    public LineIter(Reader reader, boolean isAutoClose) {
        this.bufferedReader = New.bufferedReader(reader);
        this.isAutoClose = isAutoClose;
    }

    /**
     * 计算新的节点，通过实现此方法，当调用{@link #hasNext()}时将此方法产生的节点缓存，直到调用{@link #next()}取出<br>
     * 当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    @Override
    protected String doNext() {
        try {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    if (isAutoClose) {
                        close();
                    }
                    return null;
                } else if (isValidLine(line = line.trim())) {
                    return line;
                }
            }
        } catch (IOException ioe) {
            close();
            throw new IllegalStateException(ioe.toString());
        }
    }

    /**
     * 手动结束遍历器，用于关闭操作等
     */
    @Override
    public void close() {
        ToolIO.closes(bufferedReader);
    }

    /**
     * 用于验证返回的每一行的可重写方法。
     *
     * @param line 要验证的行
     * @return 如果有效，则返回 {@code true}；从迭代器中移除，返回 {@code false}
     */
    protected boolean isValidLine(String line) {
        return Whether.noEmpty(line);
    }
}
