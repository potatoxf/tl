package pxf.tl.io.file;


import pxf.tl.api.Charsets;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.function.FunctionThrow;
import pxf.tl.help.New;
import pxf.tl.io.FileUtil;
import pxf.tl.io.LineHandler;
import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolString;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 文件读取器
 *
 * @author potatoxf
 */
public class FileReader extends FileWrapper {
    private static final long serialVersionUID = 1L;

    /**
     * 构造
     *
     * @param file     文件
     * @param charsets 编码，使用 {@link Charsets}
     */
    public FileReader(File file, Charsets charsets) {
        super(file, charsets);
        checkFile();
    }


    // ------------------------------------------------------- Constructor start

    /**
     * 构造
     *
     * @param filePath 文件路径，相对路径会被转换为相对于ClassPath的路径
     * @param charsets 编码，使用 {@link Charsets}
     */
    public FileReader(String filePath, Charsets charsets) {
        this(FileUtil.file(filePath), charsets);
    }

    /**
     * 创建 FileReader
     *
     * @param file     文件
     * @param charsets 编码，使用 {@link Charsets}
     * @return FileReader
     */
    public static FileReader create(File file, Charsets charsets) {
        return new FileReader(file, charsets);
    }

    // ------------------------------------------------------- Constructor end

    /**
     * 读取文件所有数据<br>
     * 文件的长度不能超过 {@link Integer#MAX_VALUE}
     *
     * @return 字节码
     * @throws IORuntimeException IO异常
     */
    public byte[] readBytes() throws IORuntimeException {
        long len = file.length();
        if (len >= Integer.MAX_VALUE) {
            throw new IORuntimeException("File is larger then max array size");
        }

        byte[] bytes = new byte[(int) len];
        FileInputStream in = null;
        int readLength;
        try {
            in = new FileInputStream(file);
            readLength = in.read(bytes);
            if (readLength < len) {
                throw new IOException(
                        ToolString.format("File length is [{}] but read [{}]!", len, readLength));
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        } finally {
            ToolIO.closes(in);
        }

        return bytes;
    }

    /**
     * 读取文件内容
     *
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public String readString() throws IORuntimeException {
        return New.string(readBytes(), this.charsets);
    }

    /**
     * 从文件中读取每一行数据
     *
     * @param <T>        集合类型
     * @param collection 集合
     * @return 文件中的每行内容的集合
     * @throws IORuntimeException IO异常
     */
    public <T extends Collection<String>> T readLines(T collection) throws IORuntimeException {
        BufferedReader reader = null;
        try {
            reader = FileUtil.getReader(file, charsets);
            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                collection.add(line);
            }
            return collection;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            ToolIO.closes(reader);
        }
    }

    /**
     * 按照行处理文件内容
     *
     * @param lineHandler 行处理器
     * @throws IORuntimeException IO异常
     */
    public void readLines(LineHandler lineHandler) throws IORuntimeException {
        BufferedReader reader = null;
        try {
            reader = FileUtil.getReader(file, charsets);
            ToolIO.readLines(reader, lineHandler);
        } finally {
            ToolIO.closes(reader);
        }
    }

    /**
     * 从文件中读取每一行数据
     *
     * @return 文件中的每行内容的集合
     * @throws IORuntimeException IO异常
     */
    public List<String> readLines() throws IORuntimeException {
        return readLines(new ArrayList<>());
    }

    /**
     * 按照给定的readerHandler读取文件中的数据
     *
     * @param <T>           读取的结果对象类型
     * @param readerHandler Reader处理类
     * @return 从文件中read出的数据
     * @throws IORuntimeException IO异常
     */
    public <T> T read(FunctionThrow<BufferedReader, T, IOException> readerHandler) throws IORuntimeException {
        BufferedReader reader = null;
        T result;
        try {
            reader = FileUtil.getReader(this.file, charsets);
            result = readerHandler.applyThrow(reader);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            ToolIO.closes(reader);
        }
        return result;
    }

    /**
     * 获得一个文件读取器
     *
     * @return BufferedReader对象
     * @throws IORuntimeException IO异常
     */
    public BufferedReader getReader() throws IORuntimeException {
        return New.bufferedReader(getInputStream(), this.charsets);
    }

    /**
     * 获得输入流
     *
     * @return 输入流
     * @throws IORuntimeException IO异常
     */
    public BufferedInputStream getInputStream() throws IORuntimeException {
        try {
            return new BufferedInputStream(new FileInputStream(this.file));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 将文件写入流中，此方法不会关闭比输出流
     *
     * @param out 流
     * @return 写出的流byte数
     * @throws IORuntimeException IO异常
     */
    public long writeToStream(OutputStream out) throws IORuntimeException {
        return writeToStream(out, false);
    }

    /**
     * 将文件写入流中
     *
     * @param out        流
     * @param isCloseOut 是否关闭输出流
     * @return 写出的流byte数
     * @throws IORuntimeException IO异常
     */
    public long writeToStream(OutputStream out, boolean isCloseOut) throws IORuntimeException {
        try (FileInputStream in = new FileInputStream(this.file)) {
            return ToolIO.copy(in, out);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                ToolIO.closes(out);
            }
        }
    }

    // -------------------------------------------------------------------------- Interface start

    /**
     * 检查文件
     *
     * @throws IORuntimeException IO异常
     */
    private void checkFile() throws IORuntimeException {
        if (false == file.exists()) {
            throw new IORuntimeException("File not exist: " + file);
        }
        if (false == file.isFile()) {
            throw new IORuntimeException("Not a file:" + file);
        }
    }
    // -------------------------------------------------------------------------- Interface end
}
