package pxf.tl.io.file;


import pxf.tl.api.Charsets;
import pxf.tl.io.FileUtil;

import java.io.File;
import java.io.Serializable;

/**
 * 文件包装器，扩展文件对象
 *
 * @author potatoxf
 */
public class FileWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    protected File file;
    protected Charsets charsets;

    // ------------------------------------------------------- Constructor start

    /**
     * 构造
     *
     * @param file     文件
     * @param charsets 编码，使用 {@link Charsets}
     */
    public FileWrapper(File file, Charsets charsets) {
        this.file = file;
        this.charsets = charsets;
    }
    // ------------------------------------------------------- Constructor end

    // ------------------------------------------------------- Setters and Getters start start

    /**
     * 获得文件
     *
     * @return 文件
     */
    public File getFile() {
        return file;
    }

    /**
     * 设置文件
     *
     * @param file 文件
     * @return 自身
     */
    public FileWrapper setFile(File file) {
        this.file = file;
        return this;
    }

    /**
     * 获得字符集编码
     *
     * @return 编码
     */
    public Charsets getCharset() {
        return charsets;
    }

    /**
     * 设置字符集编码
     *
     * @param charsets 编码
     * @return 自身
     */
    public FileWrapper setCharset(Charsets charsets) {
        this.charsets = charsets;
        return this;
    }
    // ------------------------------------------------------- Setters and Getters start end

    /**
     * 可读的文件大小
     *
     * @return 大小
     */
    public String readableFileSize() {
        return FileUtil.readableFileSize(file.length());
    }
}
