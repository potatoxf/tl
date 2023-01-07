package pxf.tl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.api.Charsets;
import pxf.tl.api.JavaEnvironment;
import pxf.tl.api.PoolOfCommon;
import pxf.tl.api.PoolOfString;
import pxf.tl.convert.Convert;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.exception.UtilException;
import pxf.tl.help.Assert;
import pxf.tl.help.New;
import pxf.tl.help.Valid;
import pxf.tl.help.Whether;
import pxf.tl.io.*;
import pxf.tl.io.copy.ChannelCopier;
import pxf.tl.io.copy.ReaderWriterCopier;
import pxf.tl.io.copy.StreamCopier;
import pxf.tl.iter.LineIter;
import pxf.tl.math.Hex;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

/**
 * IO工具类<br>
 * IO工具类只是辅助流的读写，并不负责关闭流。原因是流可能被多次读写，读写关闭后容易造成问题。
 *
 * @author potatoxf
 */
public final class ToolIO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolIO.class);

    private ToolIO() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    @Nonnull
    public static byte[] transferAndClose(@Nonnull InputStream inputStream) throws IOException {
        try {
            return transfer(inputStream);
        } finally {
            closes(inputStream);
        }
    }

    public static <O extends OutputStream> void transferAndClose(
            @Nonnull byte[] in, @Nonnull OutputStream out) throws IOException {
        try {
            transfer(in, out);
        } finally {
            closes(out);
        }
    }

    public static <O extends OutputStream> void transferAndClose(
            @Nonnull InputStream in, @Nonnull OutputStream out) throws IOException {
        try {
            transfer(in, out);
        } finally {
            closes(out);
        }
    }

    @Nonnull
    public static byte[] transfer(@Nonnull InputStream in) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        transfer(in, outputStream);
        return outputStream.toByteArray();
    }

    public static <O extends OutputStream> void transfer(
            @Nonnull byte[] in, @Nonnull OutputStream out) throws IOException {
        transfer(new ByteArrayInputStream(in), out);
    }

    public static <O extends OutputStream> void transfer(
            @Nonnull InputStream in, @Nonnull OutputStream out) throws IOException {
        int len;
        if (in instanceof FileInputStream) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8096);
            FileChannel channel = ((FileInputStream) in).getChannel();
            if (out instanceof FileOutputStream) {
                channel.transferTo(0, channel.size(), ((FileOutputStream) out).getChannel());
            } else {
                while ((len = channel.read(byteBuffer)) != -1) {
                    out.write(byteBuffer.array(), 0, len);
                }
            }
        } else {
            byte[] bytes = new byte[8096];
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
        }
    }

    public static void closes(@Nullable AutoCloseable... closeables) {
        if (closeables != null) {
            if (closeables.length == 1) {
                try {
                    closeables[0].close();
                } catch (Exception e) {
                    ToolLog.error(LOGGER, e, () -> "Error to close");
                }
            } else {
                for (AutoCloseable closeable : closeables) {
                    try {
                        closeable.close();
                    } catch (Exception e) {
                        ToolLog.error(LOGGER, e, () -> "Error to close");
                    }
                }
            }
        }
    }

    /**
     * 尝试关闭指定对象<br>
     * 判断对象如果实现了{@link AutoCloseable}，则调用之
     *
     * @param any 可关闭对象
     */
    public static void close(Object any) {
        if (any instanceof AutoCloseable) {
            closes((AutoCloseable) any);
        } else {
            Class<?> clz = any.getClass();
            if (clz.isArray()) {
                Class<?> componentType = clz.getComponentType();
                if (AutoCloseable.class.isAssignableFrom(componentType)) {
                    int length = Array.getLength(any);
                    if (length > 0) {
                        if (length == 1) {
                            closes((AutoCloseable) Array.get(any, 0));
                        } else if (length == 2) {
                            closes((AutoCloseable) Array.get(any, 0),
                                    (AutoCloseable) Array.get(any, 1));
                        } else if (length == 3) {
                            closes((AutoCloseable) Array.get(any, 0),
                                    (AutoCloseable) Array.get(any, 1),
                                    (AutoCloseable) Array.get(any, 2));
                        } else if (length == 4) {
                            closes((AutoCloseable) Array.get(any, 0),
                                    (AutoCloseable) Array.get(any, 1),
                                    (AutoCloseable) Array.get(any, 2),
                                    (AutoCloseable) Array.get(any, 3));
                        } else {
                            List<AutoCloseable> autoCloseables = new ArrayList<>(length);
                            for (int i = 0; i < length; i++) {
                                autoCloseables.add((AutoCloseable) Array.get(any, i));
                            }
                            closes(autoCloseables.toArray(AutoCloseable[]::new));
                        }
                    }
                }
            }
        }
    }

    /**
     * 拷贝流 thanks to:
     * https://github.com/venusdrogon/feilong-io/blob/master/src/main/java/com/feilong/io/IOWriteUtil.java
     * <br>
     * 本方法不会关闭流
     *
     * @param in             输入流
     * @param out            输出流
     * @param bufferSize     缓存大小
     * @param streamProgress 进度条
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copyByNIO(
            InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress)
            throws IORuntimeException {
        return copyByNIO(in, out, bufferSize, -1, streamProgress);
    }

    /**
     * 拷贝流<br>
     * 本方法不会关闭流
     *
     * @param in             输入流
     * @param out            输出流
     * @param bufferSize     缓存大小
     * @param count          最大长度
     * @param streamProgress 进度条
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copyByNIO(
            InputStream in, OutputStream out, int bufferSize, long count, StreamProgress streamProgress)
            throws IORuntimeException {
        return copy(
                Channels.newChannel(in), Channels.newChannel(out), bufferSize, count, streamProgress);
    }

    /**
     * 拷贝文件Channel，使用NIO，拷贝后不会关闭channel
     *
     * @param inChannel  {@link FileChannel}
     * @param outChannel {@link FileChannel}
     * @return 拷贝的字节数
     * @throws IORuntimeException IO异常
     */
    public static long copy(FileChannel inChannel, FileChannel outChannel) throws IORuntimeException {
        Assert.notNull(inChannel, "In channel is null!");
        Assert.notNull(outChannel, "Out channel is null!");

        try {
            return copySafely(inChannel, outChannel);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 文件拷贝实现
     *
     * <pre>
     * FileChannel#transferTo 或 FileChannel#transferFrom 的实现是平台相关的，需要确保低版本平台的兼容性
     * 例如 android 7以下平台在使用 ZipInputStream 解压文件的过程中，
     * 通过 FileChannel#transferFrom 传输到文件时，其返回值可能小于 totalBytes，不处理将导致文件内容缺失
     *
     * // 错误写法，dstChannel.transferFrom 返回值小于 zipEntry.getSize()，导致解压后文件内容缺失
     * try (InputStream srcStream = zipFile.getInputStream(zipEntry);
     * 		ReadableByteChannel srcChannel = Channels.newChannel(srcStream);
     * 		FileOutputStream fos = new FileOutputStream(saveFile);
     * 		FileChannel dstChannel = fos.getChannel()) {
     * 		dstChannel.transferFrom(srcChannel, 0, zipEntry.getSize());
     *  }
     * </pre>
     *
     * @param inChannel  输入通道
     * @param outChannel 输出通道
     * @return 输入通道的字节数
     * @throws IOException 发生IO错误
     * @link http://androidxref.com/6.0.1_r10/xref/libcore/luni/src/main/java/java/nio/FileChannelImpl.java
     * @link http://androidxref.com/7.0.0_r1/xref/libcore/ojluni/src/main/java/sun/nio/ch/FileChannelImpl.java
     * @link http://androidxref.com/7.0.0_r1/xref/libcore/ojluni/src/main/native/FileChannelImpl.c
     * @author potatoxf
     */
    private static long copySafely(FileChannel inChannel, FileChannel outChannel) throws IOException {
        final long totalBytes = inChannel.size();
        for (long pos = 0, remaining = totalBytes; remaining > 0; ) { // 确保文件内容不会缺失
            final long writeBytes = inChannel.transferTo(pos, remaining, outChannel); // 实际传输的字节数
            pos += writeBytes;
            remaining -= writeBytes;
        }
        return totalBytes;
    }

    /**
     * 拷贝流，使用NIO，不会关闭channel
     *
     * @param in  {@link ReadableByteChannel}
     * @param out {@link WritableByteChannel}
     * @return 拷贝的字节数
     * @throws IORuntimeException IO异常
     */
    public static long copy(ReadableByteChannel in, WritableByteChannel out)
            throws IORuntimeException {
        return copy(in, out, JavaEnvironment.DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝流，使用NIO，不会关闭channel
     *
     * @param in         {@link ReadableByteChannel}
     * @param out        {@link WritableByteChannel}
     * @param bufferSize 缓冲大小，如果小于等于0，使用默认
     * @return 拷贝的字节数
     * @throws IORuntimeException IO异常
     */
    public static long copy(ReadableByteChannel in, WritableByteChannel out, int bufferSize)
            throws IORuntimeException {
        return copy(in, out, bufferSize, null);
    }

    /**
     * 拷贝流，使用NIO，不会关闭channel
     *
     * @param in             {@link ReadableByteChannel}
     * @param out            {@link WritableByteChannel}
     * @param bufferSize     缓冲大小，如果小于等于0，使用默认
     * @param streamProgress {@link StreamProgress}进度处理器
     * @return 拷贝的字节数
     * @throws IORuntimeException IO异常
     */
    public static long copy(
            ReadableByteChannel in,
            WritableByteChannel out,
            int bufferSize,
            StreamProgress streamProgress)
            throws IORuntimeException {
        return copy(in, out, bufferSize, -1, streamProgress);
    }

    /**
     * 拷贝流，使用NIO，不会关闭channel
     *
     * @param in             {@link ReadableByteChannel}
     * @param out            {@link WritableByteChannel}
     * @param bufferSize     缓冲大小，如果小于等于0，使用默认
     * @param count          读取总长度
     * @param streamProgress {@link StreamProgress}进度处理器
     * @return 拷贝的字节数
     * @throws IORuntimeException IO异常
     */
    public static long copy(
            ReadableByteChannel in,
            WritableByteChannel out,
            int bufferSize,
            long count,
            StreamProgress streamProgress)
            throws IORuntimeException {
        return new ChannelCopier(bufferSize, count, streamProgress).copy(in, out);
    }

    /**
     * 从流中读取内容，读取完毕后并不关闭流
     *
     * @param channel  可读通道，读取完毕后并不关闭通道
     * @param charsets 字符集
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static String read(ReadableByteChannel channel, Charsets charsets)
            throws IORuntimeException {
        FastByteArrayOutputStream out = read(channel);
        return null == charsets ? out.toString() : out.toString(charsets);
    }

    /**
     * 从流中读取内容，读到输出流中
     *
     * @param channel 可读通道，读取完毕后并不关闭通道
     * @return 输出流
     * @throws IORuntimeException IO异常
     */
    public static FastByteArrayOutputStream read(ReadableByteChannel channel)
            throws IORuntimeException {
        final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        copy(channel, Channels.newChannel(out));
        return out;
    }

    /**
     * 从FileChannel中读取UTF-8编码内容
     *
     * @param fileChannel 文件管道
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static String readUtf8(FileChannel fileChannel) throws IORuntimeException {
        return read(fileChannel, Charsets.UTF_8);
    }

    /**
     * 从FileChannel中读取内容
     *
     * @param fileChannel 文件管道
     * @param charsets    字符集
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static String read(FileChannel fileChannel, Charsets charsets) throws IORuntimeException {
        MappedByteBuffer buffer;
        try {
            buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size()).load();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return New.string(buffer, charsets);
    }

    /**
     * 将Reader中的内容复制到Writer中 使用默认缓存大小，拷贝后不关闭Reader
     *
     * @param reader Reader
     * @param writer Writer
     * @return 拷贝的字节数
     * @throws IORuntimeException IO异常
     */
    public static long copy(Reader reader, Writer writer) throws IORuntimeException {
        return copy(reader, writer, JavaEnvironment.DEFAULT_BUFFER_SIZE);
    }

    /**
     * 将Reader中的内容复制到Writer中，拷贝后不关闭Reader
     *
     * @param reader     Reader
     * @param writer     Writer
     * @param bufferSize 缓存大小
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(Reader reader, Writer writer, int bufferSize) throws IORuntimeException {
        return copy(reader, writer, bufferSize, null);
    }

    /**
     * 将Reader中的内容复制到Writer中，拷贝后不关闭Reader
     *
     * @param reader         Reader
     * @param writer         Writer
     * @param bufferSize     缓存大小
     * @param streamProgress 进度处理器
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(
            Reader reader, Writer writer, int bufferSize, StreamProgress streamProgress)
            throws IORuntimeException {
        return copy(reader, writer, bufferSize, -1, streamProgress);
    }

    /**
     * 将Reader中的内容复制到Writer中，拷贝后不关闭Reader
     *
     * @param reader         Reader
     * @param writer         Writer
     * @param bufferSize     缓存大小
     * @param count          最大长度
     * @param streamProgress 进度处理器
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(
            Reader reader, Writer writer, int bufferSize, long count, StreamProgress streamProgress)
            throws IORuntimeException {
        return new ReaderWriterCopier(bufferSize, count, streamProgress).copy(reader, writer);
    }

    /**
     * 拷贝流，使用默认Buffer大小，拷贝后不关闭流
     *
     * @param in  输入流
     * @param out 输出流
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out) throws IORuntimeException {
        return copy(in, out, JavaEnvironment.DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓存大小
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize)
            throws IORuntimeException {
        return copy(in, out, bufferSize, null);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in             输入流
     * @param out            输出流
     * @param bufferSize     缓存大小
     * @param streamProgress 进度条
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(
            InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress)
            throws IORuntimeException {
        return copy(in, out, bufferSize, -1, streamProgress);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in             输入流
     * @param out            输出流
     * @param bufferSize     缓存大小
     * @param count          总拷贝长度
     * @param streamProgress 进度条
     * @return 传输的byte数
     * @throws IORuntimeException IO异常
     */
    public static long copy(
            InputStream in, OutputStream out, int bufferSize, long count, StreamProgress streamProgress)
            throws IORuntimeException {
        return new StreamCopier(bufferSize, count, streamProgress).copy(in, out);
    }

    /**
     * 拷贝文件流，使用NIO
     *
     * @param in  输入
     * @param out 输出
     * @return 拷贝的字节数
     * @throws IORuntimeException IO异常
     */
    public static long copy(FileInputStream in, FileOutputStream out) throws IORuntimeException {
        Assert.notNull(in, "FileInputStream is null!");
        Assert.notNull(out, "FileOutputStream is null!");

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = in.getChannel();
            outChannel = out.getChannel();
            return copy(inChannel, outChannel);
        } finally {
            closes(outChannel, inChannel);
        }
    }

    /**
     * 从流中读取内容，读取完毕后关闭流
     *
     * @param in       输入流，读取完毕后并不关闭流
     * @param charsets 字符集
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static String read(InputStream in, Charsets charsets) throws IORuntimeException {
        return New.string(readBytes(in), charsets);
    }

    /**
     * 从流中读取内容，读到输出流中，读取完毕后关闭流
     *
     * @param in 输入流
     * @return 输出流
     * @throws IORuntimeException IO异常
     */
    public static FastByteArrayOutputStream read(InputStream in) throws IORuntimeException {
        return read(in, true);
    }

    /**
     * 从流中读取内容，读到输出流中，读取完毕后可选是否关闭流
     *
     * @param in      输入流
     * @param isClose 读取完毕后是否关闭流
     * @return 输出流
     * @throws IORuntimeException IO异常
     */
    public static FastByteArrayOutputStream read(InputStream in, boolean isClose)
            throws IORuntimeException {
        final FastByteArrayOutputStream out;
        if (in instanceof FileInputStream) {
            // 文件流的长度是可预见的，此时直接读取效率更高
            try {
                out = new FastByteArrayOutputStream(in.available());
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } else {
            out = new FastByteArrayOutputStream();
        }
        try {
            copy(in, out);
        } finally {
            if (isClose) {
                closes(in);
            }
        }
        return out;
    }

    /**
     * 从Reader中读取String，读取完毕后关闭Reader
     *
     * @param reader Reader
     * @return String
     * @throws IORuntimeException IO异常
     */
    public static String read(Reader reader) throws IORuntimeException {
        return read(reader, true);
    }

    /**
     * 从{@link Reader}中读取String
     *
     * @param reader  {@link Reader}
     * @param isClose 是否关闭{@link Reader}
     * @return String
     * @throws IORuntimeException IO异常
     */
    public static String read(Reader reader, boolean isClose) throws IORuntimeException {
        final StringBuilder builder = ToolString.builder();
        final CharBuffer buffer = CharBuffer.allocate(JavaEnvironment.DEFAULT_BUFFER_SIZE);
        try {
            while (-1 != reader.read(buffer)) {
                builder.append(buffer.flip());
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isClose) {
                closes(reader);
            }
        }
        return builder.toString();
    }

    /**
     * 从流中读取bytes，读取完毕后关闭流
     *
     * @param in {@link InputStream}
     * @return bytes
     * @throws IORuntimeException IO异常
     */
    public static byte[] readBytes(InputStream in) throws IORuntimeException {
        return readBytes(in, true);
    }

    /**
     * 从流中读取bytes
     *
     * @param in      {@link InputStream}
     * @param isClose 是否关闭输入流
     * @return bytes
     * @throws IORuntimeException IO异常
     */
    public static byte[] readBytes(InputStream in, boolean isClose) throws IORuntimeException {
        if (in instanceof FileInputStream) {
            // 文件流的长度是可预见的，此时直接读取效率更高
            final byte[] result;
            try {
                final int available = in.available();
                result = new byte[available];
                final int readLength = in.read(result);
                if (readLength != available) {
                    throw new IOException(
                            ToolString.format("File length is [{}] but read [{}]!", available, readLength));
                }
            } catch (IOException e) {
                throw new IORuntimeException(e);
            } finally {
                if (isClose) {
                    closes(in);
                }
            }
            return result;
        }

        // 未知bytes总量的流
        return read(in, isClose).toByteArray();
    }

    /**
     * 读取指定长度的byte数组，不关闭流
     *
     * @param in     {@link InputStream}，为{@code null}返回{@code null}
     * @param length 长度，小于等于0返回空byte数组
     * @return bytes
     * @throws IORuntimeException IO异常
     */
    public static byte[] readBytes(InputStream in, int length) throws IORuntimeException {
        if (null == in) {
            return null;
        }
        if (length <= 0) {
            return new byte[0];
        }

        final FastByteArrayOutputStream out = new FastByteArrayOutputStream(length);
        copy(in, out, JavaEnvironment.DEFAULT_BUFFER_SIZE, length, null);
        return out.toByteArray();
    }

    /**
     * 读取16进制字符串
     *
     * @param in          {@link InputStream}
     * @param length      长度
     * @param toLowerCase true 传换成小写格式 ， false 传换成大写格式
     * @return 16进制字符串
     * @throws IORuntimeException IO异常
     */
    public static String readHex(InputStream in, int length, boolean toLowerCase)
            throws IORuntimeException {
        String string = Hex.encodeToString(readBytes(in, length));
        return toLowerCase?string.toLowerCase():string.toUpperCase();
    }

    /**
     * 从流中读取前28个byte并转换为16进制，字母部分使用大写
     *
     * @param in {@link InputStream}
     * @return 16进制字符串
     * @throws IORuntimeException IO异常
     */
    public static String readHex28Upper(InputStream in) throws IORuntimeException {
        return readHex(in, 28, false);
    }

    /**
     * 从流中读取前28个byte并转换为16进制，字母部分使用小写
     *
     * @param in {@link InputStream}
     * @return 16进制字符串
     * @throws IORuntimeException IO异常
     */
    public static String readHex28Lower(InputStream in) throws IORuntimeException {
        return readHex(in, 28, true);
    }

    /**
     * 从流中读取对象，即对象的反序列化
     *
     * <p>注意！！！ 此方法不会检查反序列化安全，可能存在反序列化漏洞风险！！！
     *
     * @param <T> 读取对象的类型
     * @param in  输入流
     * @return 输出流
     * @throws IORuntimeException IO异常
     * @throws UtilException      ClassNotFoundException包装
     */
    public static <T> T readObj(InputStream in) throws IORuntimeException, UtilException {
        return readObj(in, null);
    }

    /**
     * 从流中读取对象，即对象的反序列化，读取后不关闭流
     *
     * <p>注意！！！ 此方法不会检查反序列化安全，可能存在反序列化漏洞风险！！！
     *
     * @param <T>   读取对象的类型
     * @param in    输入流
     * @param clazz 读取对象类型
     * @return 输出流
     * @throws IORuntimeException IO异常
     * @throws UtilException      ClassNotFoundException包装
     */
    public static <T> T readObj(InputStream in, Class<T> clazz)
            throws IORuntimeException, UtilException {
        try {
            return readObj(
                    (in instanceof ValidateObjectInputStream)
                            ? (ValidateObjectInputStream) in
                            : new ValidateObjectInputStream(in),
                    clazz);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 从流中读取对象，即对象的反序列化，读取后不关闭流
     *
     * <p>此方法使用了{@link ValidateObjectInputStream}中的黑白名单方式过滤类，用于避免反序列化漏洞<br>
     * 通过构造{@link ValidateObjectInputStream}，调用{@link ValidateObjectInputStream#accept(Class[])}
     * 或者{@link ValidateObjectInputStream#refuse(Class[])}方法添加可以被序列化的类或者禁止序列化的类。
     *
     * @param <T>   读取对象的类型
     * @param in    输入流，使用{@link ValidateObjectInputStream}中的黑白名单方式过滤类，用于避免反序列化漏洞
     * @param clazz 读取对象类型
     * @return 输出流
     * @throws IORuntimeException IO异常
     * @throws UtilException      ClassNotFoundException包装
     */
    public static <T> T readObj(ValidateObjectInputStream in, Class<T> clazz)
            throws IORuntimeException, UtilException {
        if (in == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        if (null != clazz) {
            in.accept(clazz);
        }
        try {
            //noinspection unchecked
            return (T) in.readObject();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 从流中读取内容，使用UTF-8编码
     *
     * @param <T>        集合类型
     * @param in         输入流
     * @param collection 返回集合
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static <T extends Collection<String>> T readUtf8Lines(InputStream in, T collection)
            throws IORuntimeException {
        return readLines(in, Charsets.UTF_8, collection);
    }

    /**
     * 从流中读取内容
     *
     * @param <T>        集合类型
     * @param in         输入流
     * @param charsets   字符集
     * @param collection 返回集合
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static <T extends Collection<String>> T readLines(
            InputStream in, Charsets charsets, T collection) throws IORuntimeException {
        return readLines(New.bufferedReader(in, charsets), collection);
    }

    /**
     * 从Reader中读取内容
     *
     * @param <T>        集合类型
     * @param reader     {@link Reader}
     * @param collection 返回集合
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static <T extends Collection<String>> T readLines(Reader reader, T collection)
            throws IORuntimeException {
        readLines(reader, (LineHandler) collection::add);
        return collection;
    }

    /**
     * 按行读取UTF-8编码数据，针对每行的数据做处理
     *
     * @param in          {@link InputStream}
     * @param lineHandler 行处理接口，实现handle方法用于编辑一行的数据后入到指定地方
     * @throws IORuntimeException IO异常
     */
    public static void readUtf8Lines(InputStream in, LineHandler lineHandler)
            throws IORuntimeException {
        readLines(in, Charsets.UTF_8, lineHandler);
    }

    /**
     * 按行读取数据，针对每行的数据做处理
     *
     * @param in          {@link InputStream}
     * @param charsets    {@link Charsets}编码
     * @param lineHandler 行处理接口，实现handle方法用于编辑一行的数据后入到指定地方
     * @throws IORuntimeException IO异常
     */
    public static void readLines(InputStream in, Charsets charsets, LineHandler lineHandler)
            throws IORuntimeException {
        readLines(New.bufferedReader(in, charsets), lineHandler);
    }

    /**
     * 按行读取数据，针对每行的数据做处理<br>
     * {@link Reader}自带编码定义，因此读取数据的编码跟随其编码。<br>
     * 此方法不会关闭流，除非抛出异常
     *
     * @param reader      {@link Reader}
     * @param lineHandler 行处理接口，实现handle方法用于编辑一行的数据后入到指定地方
     * @throws IORuntimeException IO异常
     */
    public static void readLines(Reader reader, LineHandler lineHandler) throws IORuntimeException {
        Assert.notNull(reader);
        Assert.notNull(lineHandler);

        for (String line : lineIter(reader)) {
            lineHandler.handle(line);
        }
    }

    /**
     * 将byte[]写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param content    写入的内容
     * @throws IORuntimeException IO异常
     */
    public static void write(OutputStream out, boolean isCloseOut, byte[] content)
            throws IORuntimeException {
        try {
            out.write(content);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                closes(out);
            }
        }
    }

    /**
     * 将多部分内容写到流中，自动转换为UTF-8字符串
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容，调用toString()方法，不包括不会自动换行
     * @throws IORuntimeException IO异常
     */
    public static void writeUtf8(OutputStream out, boolean isCloseOut, Object... contents)
            throws IORuntimeException {
        write(out, Charsets.UTF_8, isCloseOut, contents);
    }

    /**
     * 将多部分内容写到流中，自动转换为字符串
     *
     * @param out        输出流
     * @param charsets   写出的内容的字符集
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容，调用toString()方法，不包括不会自动换行
     * @throws IORuntimeException IO异常
     */
    public static void write(
            OutputStream out, Charsets charsets, boolean isCloseOut, Object... contents)
            throws IORuntimeException {
        OutputStreamWriter osw = null;
        try {
            osw = New.outputStreamWriter(out, charsets);
            for (Object content : contents) {
                if (content != null) {
                    osw.write(Convert.toStr(content, ToolString.EMPTY));
                }
            }
            osw.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                closes(osw);
            }
        }
    }

    /**
     * 将多部分内容写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param obj        写入的对象内容
     * @throws IORuntimeException IO异常
     */
    public static void writeObj(OutputStream out, boolean isCloseOut, Serializable obj)
            throws IORuntimeException {
        writeObjects(out, isCloseOut, obj);
    }

    /**
     * 将多部分内容写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容
     * @throws IORuntimeException IO异常
     */
    public static void writeObjects(OutputStream out, boolean isCloseOut, Serializable... contents)
            throws IORuntimeException {
        ObjectOutputStream osw = null;
        try {
            osw =
                    out instanceof ObjectOutputStream
                            ? (ObjectOutputStream) out
                            : new ObjectOutputStream(out);
            for (Object content : contents) {
                if (content != null) {
                    osw.writeObject(content);
                }
            }
            osw.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                closes(osw);
            }
        }
    }

    /**
     * 从缓存中刷出数据
     *
     * @param flushable {@link Flushable}
     */
    public static void flush(Flushable flushable) {
        if (null != flushable) {
            try {
                flushable.flush();
            } catch (Exception e) {
                // 静默刷出
            }
        }
    }

    /**
     * 对比两个流内容是否相同<br>
     * 内部会转换流为 {@link BufferedInputStream}
     *
     * @param input1 第一个流
     * @param input2 第二个流
     * @return 两个流的内容一致返回true，否则false
     * @throws IORuntimeException IO异常
     */
    public static boolean contentEquals(InputStream input1, InputStream input2)
            throws IORuntimeException {
        if (false == (input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (false == (input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }

        try {
            int ch = input1.read();
            while (PoolOfCommon.EOF != ch) {
                int ch2 = input2.read();
                if (ch != ch2) {
                    return false;
                }
                ch = input1.read();
            }

            int ch2 = input2.read();
            return ch2 == PoolOfCommon.EOF;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 对比两个Reader的内容是否一致<br>
     * 内部会转换流为 {@link BufferedInputStream}
     *
     * @param input1 第一个reader
     * @param input2 第二个reader
     * @return 两个流的内容一致返回true，否则false
     * @throws IORuntimeException IO异常
     */
    public static boolean contentEquals(Reader input1, Reader input2) throws IORuntimeException {
        input1 = New.bufferedReader(input1);
        input2 = New.bufferedReader(input2);

        try {
            int ch = input1.read();
            while (PoolOfCommon.EOF != ch) {
                int ch2 = input2.read();
                if (ch != ch2) {
                    return false;
                }
                ch = input1.read();
            }

            int ch2 = input2.read();
            return ch2 == PoolOfCommon.EOF;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 对比两个流内容是否相同，忽略EOL字符<br>
     * 内部会转换流为 {@link BufferedInputStream}
     *
     * @param input1 第一个流
     * @param input2 第二个流
     * @return 两个流的内容一致返回true，否则false
     * @throws IORuntimeException IO异常
     */
    public static boolean contentEqualsIgnoreEOL(Reader input1, Reader input2)
            throws IORuntimeException {
        final BufferedReader br1 = New.bufferedReader(input1);
        final BufferedReader br2 = New.bufferedReader(input2);

        try {
            String line1 = br1.readLine();
            String line2 = br2.readLine();
            while (line1 != null && line1.equals(line2)) {
                line1 = br1.readLine();
                line2 = br2.readLine();
            }
            return Objects.equals(line1, line2);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 计算流CRC32校验码，计算后关闭流
     *
     * @param in 文件，不能为目录
     * @return CRC32值
     * @throws IORuntimeException IO异常
     */
    public static long checksumCRC32(InputStream in) throws IORuntimeException {
        return checksum(in, new CRC32()).getValue();
    }

    /**
     * 计算流的校验码，计算后关闭流
     *
     * @param in       流
     * @param checksum {@link Checksum}
     * @return Checksum
     * @throws IORuntimeException IO异常
     */
    public static Checksum checksum(InputStream in, Checksum checksum) throws IORuntimeException {
        Assert.notNull(in, "InputStream is null !");
        if (null == checksum) {
            checksum = new CRC32();
        }
        try {
            in = new CheckedInputStream(in, checksum);
            copy(in, new NullOutputStream());
        } finally {
            closes(in);
        }
        return checksum;
    }

    /**
     * 计算流的校验码，计算后关闭流
     *
     * @param in       流
     * @param checksum {@link Checksum}
     * @return Checksum
     * @throws IORuntimeException IO异常
     */
    public static long checksumValue(InputStream in, Checksum checksum) {
        return checksum(in, checksum).getValue();
    }

    /**
     * 返回行遍历器
     *
     * <pre>
     * LineIterator it = null;
     * try {
     * 	it = IoUtil.lineIter(reader);
     * 	while (it.hasNext()) {
     * 		String line = it.nextLine();
     * 		// do something with line
     *    }
     * } finally {
     * 		it.close();
     * }
     * </pre>
     *
     * @param reader {@link Reader}
     * @return {@link LineIter}
     */
    public static LineIter lineIter(Reader reader) {
        return new LineIter(reader);
    }

    /**
     * 返回行遍历器
     *
     * <pre>
     * LineIterator it = null;
     * try {
     * 	it = IoUtil.lineIter(in, Charsets.UTF8.get());
     * 	while (it.hasNext()) {
     * 		String line = it.nextLine();
     * 		// do something with line
     *    }
     * } finally {
     * 		it.close();
     * }
     * </pre>
     *
     * @param in       {@link InputStream}
     * @param charsets 编码
     * @return {@link LineIter}
     */
    public static LineIter lineIter(InputStream in, Charsets charsets) {
        return new LineIter(in, charsets);
    }

    /**
     * 从流中读取UTF8编码的内容
     *
     * @param in 输入流
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static String readUtf8(InputStream in) throws IORuntimeException {
        return read(in, Charsets.UTF_8);
    }

    public static String readAllString(File file) throws IOException {
        return readAllString(file, null);
    }

    public static String readAllString(File file, Charsets charsets) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            return readAllString(fileInputStream, charsets);
        } finally {
            closes(fileInputStream);
        }
    }

    public static String readAllString(URL url) throws IOException {
        return readAllString(url.openStream(), Charsets.UTF_8);
    }

    public static String readAllString(URL url, Charsets charsets) throws IOException {
        return readAllString(url.openStream(), charsets);
    }

    public static String readAllString(InputStream inputStream) throws IOException {
        return readAllString(inputStream, Charsets.UTF_8);
    }

    public static String readAllString(InputStream inputStream, Charsets charsets)
            throws IOException {
        char[] results = readAllChars(inputStream, charsets);
        if (Whether.empty(results)) {
            return PoolOfString.EMPTY;
        }
        return new String(results);
    }

    public static String readAllString(Reader reader) throws IOException {
        char[] results = readAllChars(reader);
        if (Whether.empty(results)) {
            return PoolOfString.EMPTY;
        }
        return new String(results);
    }

    public static char[] readAllChars(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        try {
            return readAllChars(fileReader);
        } finally {
            closes(fileReader);
        }
    }

    public static char[] readAllChars(Reader reader) throws IOException {
        CharArrayWriter charArrayWriter = new CharArrayWriter();
        char[] buffer = new char[JavaEnvironment.DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            charArrayWriter.write(buffer, 0, read);
        }
        return charArrayWriter.toCharArray();
    }

    public static char[] readAllChars(InputStream inputStream, Charsets charsets)
            throws IOException {
        return readAllChars(New.inputStreamReader(inputStream, charsets));
    }

    public static byte[] readAllBytes(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            return readAllBytes(fileInputStream);
        } finally {
            closes(fileInputStream);
        }
    }

    public static byte[] readAllBytes(JarFile jarFile, JarEntry jarEntry) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = jarFile.getInputStream(jarEntry);
            return readAllBytes(inputStream);
        } finally {
            closes(inputStream);
        }
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[JavaEnvironment.DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, read);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void writeTextData(
            File file, Iterable<?> data, String split, Charsets charsets) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            writeTextData(fileOutputStream, data.iterator(), split, charsets);
        }
    }

    public static void writeTextData(
            OutputStream outputStream, Iterable<?> data, String split, Charsets charsets)
            throws IOException {
        writeTextData(outputStream, data.iterator(), split, charsets);
    }

    public static void writeTextData(
            File file, Iterator<?> data, String split, Charsets charsets) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            writeTextData(new FileOutputStream(file), data, split, charsets);
        }
    }

    public static void writeTextData(
            OutputStream outputStream, Iterator<?> data, String split, Charsets charsets)
            throws IOException {
        if (data == null) {
            return;
        }
        Charset charset = Valid.val(charsets, Charsets.UTF_8).get();
        byte[] splitBytes = Valid.val(split, JavaEnvironment.LINE_SEPARATOR).getBytes(charset);
        Object datum;
        while (data.hasNext()) {
            datum = data.next();
            if (datum != null) {
                outputStream.write(datum.toString().getBytes(charset));
            }
            outputStream.write(splitBytes);
        }
    }

    public static void writeTextData(File file, Iterable<?> data, String split) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            writeTextData(fileWriter, data.iterator(), split);
        }
    }

    public static void writeTextData(Writer writer, Iterable<?> data, String split)
            throws IOException {
        writeTextData(writer, data.iterator(), split);
    }

    public static void writeTextData(File file, Iterator<?> data, String split) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            writeTextData(new FileWriter(file), data, split);
        }
    }

    public static void writeTextData(Writer writer, Iterator<?> data, String split)
            throws IOException {
        if (data == null) {
            return;
        }
        split = Valid.val(split, JavaEnvironment.LINE_SEPARATOR);
        Object datum;
        while (data.hasNext()) {
            datum = data.next();
            if (datum != null) {
                writer.write(datum.toString());
            }
            writer.write(split);
        }
    }

    public static void write(File file, String string, Charsets charsets) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            write(fileOutputStream, new ByteArrayInputStream(string.getBytes(charsets.get())));
        }
    }

    public static void write(File file, byte[] bytes) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            write(fileOutputStream, bytes);
        }
    }

    public static void write(File file, InputStream inputStream) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            write(fileOutputStream, inputStream);
        }
    }

    public static void write(File file, char[] chars) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            write(fileWriter, chars);
        }
    }

    public static void write(File file, Reader reader) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            write(fileWriter, reader);
        }
    }

    public static void write(OutputStream outputStream, File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            write(outputStream, fileInputStream);
        }
    }

    public static void write(OutputStream outputStream, byte[] bytes) throws IOException {
        outputStream.write(bytes);
    }

    public static void write(OutputStream outputStream, byte[] bytes, int off, int len)
            throws IOException {
        outputStream.write(bytes, off, len);
    }

    public static void write(OutputStream outputStream, InputStream inputStream) throws IOException {
        byte[] cache = new byte[JavaEnvironment.DEFAULT_BUFFER_SIZE];
        int len;
        while ((len = inputStream.read(cache)) > 0) {
            outputStream.write(cache, 0, len);
        }
    }

    public static void write(Writer writer, char[] chars) throws IOException {
        writer.write(chars);
    }

    public static void write(Writer writer, char[] chars, int off, int len) throws IOException {
        writer.write(chars, off, len);
    }

    public static void write(Writer writer, Reader reader) throws IOException {
        char[] cache = new char[JavaEnvironment.DEFAULT_BUFFER_SIZE];
        int len;
        while ((len = reader.read(cache)) > 0) {
            writer.write(cache, 0, len);
        }
    }
}
