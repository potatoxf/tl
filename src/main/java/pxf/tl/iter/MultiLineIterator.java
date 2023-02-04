package pxf.tl.iter;


import pxf.tl.api.Charsets;
import pxf.tl.exception.IODetailException;
import pxf.tl.util.ToolIO;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * 行迭代器
 *
 * @author potatoxf
 */
public class MultiLineIterator extends AbstractIter<String> {

    /**
     * 最小缓存
     */
    public static final int MIN_CACHE_SIZE = 1024;
    /**
     * 默认缓存1M
     */
    private static final int DEFAULT_CACHE_SIZE = 1024 * 1024;
    /**
     * 路径迭代器
     */
    private final Iterator<String> pathIterator;
    /**
     * 缓存大小
     */
    private final int cacheSize;
    /**
     * 字符集
     */
    private final Charsets charsets;
    /**
     * 是否异常终止
     */
    private final boolean isExceptionTerminal;
    /**
     * 当前行读取器
     */
    private LineNumberReader currentLineNumberReader;
    /**
     * 当前路径
     */
    private String currentPath;
    /**
     * 当前行号
     */
    private int currentNumber;

    /**
     * @param paths
     * @param isExceptionTerminal
     * @param cacheSize
     * @param charsets
     */
    public MultiLineIterator(
            Collection<String> paths,
            boolean isExceptionTerminal,
            int cacheSize,
            Charsets charsets) {
        this(paths.iterator(), isExceptionTerminal, cacheSize, charsets);
    }

    /**
     * @param paths
     * @param isExceptionTerminal
     * @param cacheSize
     * @param charsets
     */
    public MultiLineIterator(
            Iterator<String> paths,
            boolean isExceptionTerminal,
            int cacheSize,
            Charsets charsets) {
        if (!paths.hasNext()) {
            throw new IllegalArgumentException("Contains at least one file path");
        }
        if (cacheSize < MIN_CACHE_SIZE) {
            cacheSize = DEFAULT_CACHE_SIZE;
        }
        if (charsets == null) {
            charsets = Charsets.UTF_8;
        }
        this.pathIterator = paths;
        this.isExceptionTerminal = isExceptionTerminal;
        this.cacheSize = cacheSize;
        this.charsets = charsets;
    }

    /**
     * 获取当前行号
     *
     * @return 返回当前行号，如果返回 {@code 0}则表示未读
     */
    public int getCurrentNumber() {
        return currentNumber;
    }

    /**
     * 获取当前文件
     *
     * @return 获取当前行号，如果返回 {@code null}则表示未读
     */
    public String getCurrentPath() {
        return currentPath == null ? null : currentPath;
    }

    /**
     * 计算新的节点，通过实现此方法，当调用{@link #hasNext()}时将此方法产生的节点缓存，直到调用{@link #next()}取出<br>
     * 当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    @Override
    protected String doNext() {
        String currentCacheLine;
        while (currentLineNumberReader != null || pathIterator.hasNext()) {
            if (currentLineNumberReader == null) {
                currentPath = pathIterator.next();
                currentLineNumberReader = buildCurrentLineNumberReader();
            }
            if (currentLineNumberReader == null) {
                continue;
            }
            currentCacheLine = readCurrentLine();
            if (currentCacheLine != null) {
                return currentCacheLine;
            }
            ToolIO.closes(currentLineNumberReader);
            currentLineNumberReader = null;
            currentNumber = 0;
        }
        return null;
    }

    /**
     * @return
     */
    private LineNumberReader buildCurrentLineNumberReader() {
        try {
            return new LineNumberReader(
                    new InputStreamReader(new FileInputStream(currentPath), charsets.get()), cacheSize);
        } catch (FileNotFoundException e) {
            if (isExceptionTerminal) {
                String msg = String.format("The file [%s] not found, will be skipped", currentPath);
                if (isExceptionTerminal) {
                    throw new IODetailException(msg, e, currentPath);
                }
            }
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return
     */
    private String readCurrentLine() {
        currentNumber++;
        try {
            return currentLineNumberReader.readLine();
        } catch (IOException e) {
            if (isExceptionTerminal) {
                String msg =
                        String.format(
                                "An exception occurred while reading the file [%s] at line [%d] and will be skipped",
                                currentPath, currentNumber);
                if (isExceptionTerminal) {
                    throw new IODetailException(msg, e, currentPath);
                }
            }
            e.printStackTrace();
        }
        return null;
    }
}
