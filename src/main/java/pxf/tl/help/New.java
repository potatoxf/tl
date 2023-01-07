package pxf.tl.help;

import pxf.tl.api.*;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.exception.UnsupportedException;
import pxf.tl.function.FunctionThrow;
import pxf.tl.io.BOMInputStream;
import pxf.tl.io.BOMReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public interface New {

    @Nullable
    static File file(@Nonnull URL url) {
        return file(url, Charsets.defaultCharsets());
    }

    @Nullable
    static File file(@Nonnull URL url, @Nullable Charsets charsets) {
        File file;
        String path;

        try {
            path = url.toURI().getSchemeSpecificPart();
            if ((file = new File(path)).exists()) {
                return file;
            }
        } catch (URISyntaxException ignored) {
        }

        if (charsets == null) {
            path = URLDecoder.decode(url.getPath(), Charsets.defaultCharsets().get());
        } else {
            Charset charset = charsets.get();
            if (charset == null) {
                path = URLDecoder.decode(url.getPath(), Charsets.defaultCharsets().get());
            } else {
                path = URLDecoder.decode(url.getPath(), charset);
            }
        }
        if (path.contains(".jar!")) {
            path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
        }
        if ((file = new File(path)).exists()) return file;

        try {
            path = url.toExternalForm();
            if (path.startsWith("jar:")) path = path.substring("jar:".length());
            if (path.startsWith("wsjar:")) path = path.substring("wsjar:".length());
            if (path.startsWith("file:")) path = path.substring("file:".length());
            if (path.contains(".jar!")) path = path.substring(0, path.indexOf(".jar!") + ".jar".length());
            if (path.contains(".war!")) path = path.substring(0, path.indexOf(".war!") + ".war".length());
            if ((file = new File(path)).exists()) return file;

            path = path.replace("%20", " ");
            if ((file = new File(path)).exists()) return file;

        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 从URL中获取流
     *
     * @param url {@link URL}
     * @return InputStream流
     */
    static InputStream inputStream(@Nonnull URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 转URL为URI
     *
     * @param url URL
     * @return URI
     */
    @Nonnull
    static URI uri(@Nonnull URL url) {
        return uri(url, null);
    }

    /**
     * 转URL为URI
     *
     * @param url      URL
     * @param charsets 是否编码参数中的特殊字符（默认UTF-8编码）
     * @return URI
     */
    @Nonnull
    static URI uri(@Nonnull URL url, @Nullable Charsets charsets) {
        return uri(url.toString(), charsets);
    }


    /**
     * 转字符串为URI
     *
     * @param location 字符串路径
     * @return URI
     */
    @Nonnull
    static URI uri(@Nonnull String location) {
        return uri(location, null);
    }

    /**
     * 转字符串为URI
     *
     * @param location 字符串路径
     * @param charsets 是否编码参数中的特殊字符（默认UTF-8编码）
     * @return URI
     */
    @Nonnull
    static URI uri(@Nonnull String location, @Nullable Charsets charsets) {
        try {

            if (charsets == null) {
                return new URI(location);
            } else {
                Charset charset = charsets.get();
                if (charset == null) {
                    return new URI(location);
                } else {
                    return new URI(URLEncoder.encode(location, charset));
                }
            }
        } catch (URISyntaxException e) {
            throw new UnsupportedException("URI[" + location + "]", e);
        }
    }

    @Nonnull
    static File file(@Nonnull String file) {
        return New.file(new File(file));
    }

    @Nonnull
    static File file(@Nonnull File file) {
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.isDirectory()) {
                for (int i = 1; i <= 3; i++) { // 高并发场景下，可以看到 i 处于 1 ~ 3 之间
                    // 如果文件已存在，也会返回 false，所以该值不能作为是否能创建的依据，因此不对其进行处理
                    //noinspection ResultOfMethodCallIgnored
                    dir.mkdirs();
                    if (dir.exists()) {
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return file;
    }

    @Nonnull
    static File dir(@Nonnull String dir) {
        return New.dir(new File(dir));
    }

    @Nonnull
    static File dir(@Nonnull File dir) {
        if (!dir.exists()) {
            if (!dir.isDirectory()) {
                for (int i = 1; i <= 3; i++) { // 高并发场景下，可以看到 i 处于 1 ~ 3 之间
                    // 如果文件已存在，也会返回 false，所以该值不能作为是否能创建的依据，因此不对其进行处理
                    //noinspection ResultOfMethodCallIgnored
                    dir.mkdirs();
                    if (dir.exists()) {
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return dir;
    }

    @Nonnull
    static BufferedReader bufferedOutputStream(@Nonnull Reader input) {
        if (input instanceof BufferedReader) {
            return (BufferedReader) input;
        } else {
            return new BufferedReader(input);
        }
    }

    @Nonnull
    static FileOutputStream fileOutputStream(@Nonnull String file) {
        return fileOutputStream(new File(file));
    }

    @Nonnull
    static FileOutputStream fileOutputStream(@Nonnull File file) {
        return fileOutputStream(file, false);
    }

    @Nonnull
    static FileOutputStream fileOutputStream(@Nonnull File file, boolean isAppend) {
        New.file(file);
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file, isAppend);
        } catch (FileNotFoundException e) {
            throw new IORuntimeException("Unable to create FileOutputStream", e, file.getPath());
        }
        return fileOutputStream;
    }

    @Nonnull
    static FileInputStream fileInputStream(@Nonnull String file) {
        return fileInputStream(new File(file));
    }

    @Nonnull
    static FileInputStream fileInputStream(@Nonnull File file) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IORuntimeException("Unable to create FileInputStream", e, file.getPath());
        }
        return fileInputStream;
    }

    @Nonnull
    static InputStreamReader inputStreamReader(@Nonnull InputStream inputStream, Charsets charsets) {
        InputStreamReader reader;
        if (charsets == null) {
            reader = new InputStreamReader(inputStream);
        } else {
            Charset charset = charsets.get();
            if (charset == null) {
                reader = new InputStreamReader(inputStream);
            } else {
                reader = new InputStreamReader(inputStream, charset);
            }
        }
        return reader;
    }

    @Nonnull
    static OutputStreamWriter outputStreamWriter(@Nonnull OutputStream outputStream, Charsets charsets) {
        OutputStreamWriter writer;
        if (charsets == null) {
            writer = new OutputStreamWriter(outputStream);
        } else {
            Charset charset = charsets.get();
            if (charset == null) {
                writer = new OutputStreamWriter(outputStream);
            } else {
                writer = new OutputStreamWriter(outputStream, charsets.get());
            }
        }
        return writer;
    }

    @Nonnull
    static FileReader fileReader(@Nonnull File file, Charsets charsets) {
        FileReader reader;
        try {
            if (charsets == null) {
                reader = new FileReader(file);
            } else {
                Charset charset = charsets.get();
                if (charset == null) {
                    reader = new FileReader(file);
                } else {
                    reader = new FileReader(file, charset);
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return reader;
    }

    @Nonnull
    static FileWriter fileWriter(@Nonnull File file, Charsets charsets) {
        FileWriter writer;
        try {
            if (charsets == null) {
                writer = new FileWriter(file);
            } else {
                Charset charset = charsets.get();
                if (charset == null) {
                    writer = new FileWriter(file);
                } else {
                    writer = new FileWriter(file, charset);
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return writer;
    }

    @Nonnull
    static BufferedOutputStream bufferedOutputStream(@Nonnull OutputStream outputStream) {
        if (outputStream instanceof BufferedOutputStream) {
            return (BufferedOutputStream) outputStream;
        } else {
            return new BufferedOutputStream(outputStream, JavaEnvironment.DEFAULT_BUFFER_SIZE);
        }
    }

    @Nonnull
    static BufferedInputStream bufferedInputStream(@Nonnull InputStream inputStream) {
        if (inputStream instanceof BufferedInputStream) {
            return (BufferedInputStream) inputStream;
        } else {
            return new BufferedInputStream(inputStream, JavaEnvironment.DEFAULT_BUFFER_SIZE);
        }
    }

    @Nonnull
    static BufferedWriter bufferedWriterWithUTF8(@Nonnull OutputStream out) {
        return bufferedWriter(out, Charsets.UTF_8);
    }

    @Nonnull
    static BufferedWriter bufferedWriter(@Nonnull OutputStream out, Charsets charsets) {
        OutputStreamWriter writer;
        if (null == charsets) {
            writer = new OutputStreamWriter(out);
        } else {
            Charset charset = charsets.get();
            if (null == charset) {
                writer = new OutputStreamWriter(out);
            } else {
                writer = new OutputStreamWriter(out, charset);
            }
        }

        return new BufferedWriter(writer);
    }

    @Nonnull
    static BufferedWriter bufferedWriter(@Nonnull Writer writer) {
        if (writer instanceof BufferedWriter) {
            return (BufferedWriter) writer;
        } else {
            return new BufferedWriter(writer, JavaEnvironment.DEFAULT_BUFFER_SIZE);
        }
    }

    @Nonnull
    static BufferedReader bufferedReaderWithUTF8(@Nonnull InputStream in) {
        return bufferedReader(in, Charsets.UTF_8);
    }

    @Nonnull
    static BufferedReader bufferedReader(@Nonnull InputStream in, Charsets charsets) {

        InputStreamReader reader;
        if (null == charsets) {
            reader = new InputStreamReader(in);
        } else {
            Charset charset = charsets.get();
            if (null == charset) {
                reader = new InputStreamReader(in);
            } else {
                reader = new InputStreamReader(in, charset);
            }
        }

        return new BufferedReader(reader);
    }

    @Nonnull
    static BufferedReader bufferedReader(@Nonnull Reader reader) {
        if (reader instanceof BufferedReader) {
            return (BufferedReader) reader;
        } else {
            return new BufferedReader(reader, JavaEnvironment.DEFAULT_BUFFER_SIZE);
        }
    }

    @Nonnull
    static PushbackReader pushBackReader(@Nonnull Reader reader, int pushBackSize) {
        return (reader instanceof PushbackReader)
                ? (PushbackReader) reader
                : new PushbackReader(reader, pushBackSize);
    }

    @Nonnull
    static PushbackInputStream pushbackInputStream(@Nonnull InputStream in, int pushBackSize) {
        return (in instanceof PushbackInputStream)
                ? (PushbackInputStream) in
                : new PushbackInputStream(in, pushBackSize);
    }

    @Nonnull
    static BufferedReader bufferedReader(@Nonnull BOMInputStream in) {
        return bufferedReader(in, in.getCharsets());
    }


    @Nonnull
    static ByteArrayInputStream byteArrayInputStreamWithUTF8(@Nullable String content) {
        return byteArrayInputStream(content, Charsets.UTF_8);
    }

    @Nonnull
    static ByteArrayInputStream byteArrayInputStream(@Nullable String content, @Nullable Charsets charsets) {
        if (content == null) {
            return PoolOfObject.EMPTY_BYTE_ARRAY_INPUT_STREAM;
        }
        byte[] bytes;
        if (charsets == null) {
            bytes = content.getBytes();
        } else {
            Charset charset = charsets.get();
            if (charset == null) {
                bytes = content.getBytes();
            } else {
                bytes = content.getBytes(charset);
            }
        }
        return byteArrayInputStream(bytes);
    }

    @Nonnull
    static ByteArrayInputStream byteArrayInputStream(@Nullable byte[] content) {
        if (content == null || content.length == 0) {
            return PoolOfObject.EMPTY_BYTE_ARRAY_INPUT_STREAM;
        }
        return new ByteArrayInputStream(content);
    }

    @Nonnull
    static ByteArrayInputStream byteArrayInputStream(@Nullable ByteArrayOutputStream out) {
        if (out == null || out.size() == 0) {
            return PoolOfObject.EMPTY_BYTE_ARRAY_INPUT_STREAM;
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Nonnull
    static BOMReader bomReader(@Nonnull InputStream in) {
        return new BOMReader(in);
    }

    /**
     * 将指定{@link InputStream} 转换为{@link InputStream#available()}方法可用的流。<br>
     * 在Socket通信流中，服务端未返回数据情况下{@link InputStream#available()}方法始终为{@code 0}<br>
     * 因此，在读取前需要调用{@link InputStream#read()}读取一个字节（未返回会阻塞），一旦读取到了，{@link
     * InputStream#available()}方法就正常了。<br>
     * 需要注意的是，在网络流中，是按照块来传输的，所以 {@link InputStream#available()} 读取到的并非最终长度，而是此次块的长度。<br>
     * 此方法返回对象的规则为：
     *
     * <ul>
     *   <li>FileInputStream 返回原对象，因为文件流的available方法本身可用
     *   <li>其它InputStream 返回PushbackInputStream
     * </ul>
     *
     * @param in 被转换的流
     * @return 转换后的流，可能为{@link PushbackInputStream}
     */
    @Nonnull
    static InputStream availableStream(@Nonnull InputStream in) {
        if (in instanceof FileInputStream) {
            // FileInputStream本身支持available方法。
            return in;
        }

        final PushbackInputStream pushbackInputStream = pushbackInputStream(in, 1);
        try {
            final int available = pushbackInputStream.available();
            if (available <= 0) {
                // 此操作会阻塞，直到有数据被读到
                int b = pushbackInputStream.read();
                pushbackInputStream.unread(b);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }

        return pushbackInputStream;
    }

    /**
     * 将{@link InputStream}转换为支持mark标记的流<br>
     * 若原流支持mark标记，则返回原流，否则使用{@link BufferedInputStream} 包装之
     *
     * @param in 流
     * @return {@link InputStream}
     */
    @Nonnull
    static InputStream markSupportStream(@Nonnull InputStream in) {
        if (!in.markSupported()) {
            return new BufferedInputStream(in);
        }
        return in;
    }

    /**
     * 将{@link InputStream}转换为支持mark标记的流<br>
     * 若原流支持mark标记，则返回原流，否则使用{@link BufferedInputStream} 包装之
     *
     * @param reader 流
     * @return {@link InputStream}
     */
    @Nonnull
    static Reader markSupportReader(@Nonnull Reader reader) {
        if (!reader.markSupported()) {
            return new PushbackReader(reader);
        }
        return reader;
    }

    @Nonnull
    static ZipFile zipFile(@Nonnull File file, @Nullable Charsets charsets) {
        ZipFile zipFile;
        try {
            if (charsets == null) {
                zipFile = new ZipFile(file);
            } else {
                Charset charset = charsets.get();
                if (charset == null) {
                    zipFile = new ZipFile(file);
                } else {
                    zipFile = new ZipFile(file, charset);
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return zipFile;
    }

    @Nonnull
    static ZipOutputStream zipOutputStream(@Nonnull File file, @Nullable Charsets charsets) {
        return New.zipOutputStream(New.fileOutputStream(file), charsets);
    }

    @Nonnull
    static ZipOutputStream zipOutputStream(@Nonnull OutputStream outputStream, @Nullable Charsets charsets) {
        if (outputStream instanceof ZipOutputStream) {
            return (ZipOutputStream) outputStream;
        }
        if (charsets == null) {
            return new ZipOutputStream(outputStream);
        } else {
            Charset charset = charsets.get();
            if (charset == null) {
                return new ZipOutputStream(outputStream);
            } else {
                return new ZipOutputStream(outputStream, charset);
            }
        }
    }

    @Nonnull
    static ZipInputStream zipInputStream(@Nonnull File file, @Nullable Charsets charsets) {
        return New.zipInputStream(New.fileInputStream(file), charsets);
    }

    @Nonnull
    static ZipInputStream zipInputStream(@Nonnull InputStream in, @Nullable Charsets charsets) {
        if (in instanceof ZipInputStream) {
            return (ZipInputStream) in;
        }
        if (charsets == null) {
            return new ZipInputStream(in);
        } else {
            Charset charset = charsets.get();
            if (charset == null) {
                return new ZipInputStream(in);
            } else {
                return new ZipInputStream(in, charset);
            }
        }
    }

    @Nonnull
    static String string(@Nullable CharSequence charSequence) {
        if (charSequence == null) {
            return PoolOfString.EMPTY;
        } else {
            return charSequence.toString();
        }
    }

    @Nonnull
    static String string(@Nonnull String string, @Nullable Charsets srcCharsets, @Nullable Charsets destCharsets) {
        if (srcCharsets == null || destCharsets == null) {
            return string;
        } else {
            Charset srcCharset = srcCharsets.get();
            Charset destCharset = destCharsets.get();
            return new String(string.getBytes(srcCharset), destCharset);
        }
    }

    @Nonnull
    static String string(@Nonnull byte[] bytes, @Nullable Charsets charsets) {
        if (charsets == null) {
            return new String(bytes);
        } else {
            Charset charset = charsets.get();
            if (charset == null) {
                return new String(bytes);
            } else {
                return new String(bytes, charset);
            }
        }
    }

    @Nonnull
    static String string(@Nonnull ByteArrayOutputStream byteArrayOutputStream, @Nullable Charsets charsets) {
        return string(byteArrayOutputStream.toByteArray(), charsets);
    }

    @Nonnull
    static String string(@Nonnull ByteBuffer byteBuffer, @Nullable Charsets charsets) {
        if (charsets == null) {
            return new String(byteBuffer.array());
        } else {
            Charset charset = charsets.get();
            if (charset == null) {
                return new String(byteBuffer.array());
            } else {
                return charset.decode(byteBuffer).toString();
            }
        }
    }

    @Nonnull
    static StringReader stringReader(@Nullable CharSequence charSequence) {
        if (null == charSequence) {
            return new StringReader("");
        }
        return new StringReader(charSequence.toString());
    }

    @Nonnull
    static StringWriter stringWriter() {
        return new StringWriter();
    }

    /**
     * 新建一个空List
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @return List对象
     */
    static <T> List<T> list(boolean isLinked) {
        return isLinked ? new LinkedList<>() : new ArrayList<>();
    }

    /**
     * 新建一个List
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param values   数组
     * @return List对象
     */
    @SafeVarargs
    static <T> List<T> list(boolean isLinked, T... values) {
        if (Whether.empty(values)) {
            return list(isLinked);
        }
        final List<T> arrayList = isLinked ? new LinkedList<>() : new ArrayList<>(values.length);
        Collections.addAll(arrayList, values);
        return arrayList;
    }

    /**
     * 新建一个List
     *
     * @param <T>         集合元素类型
     * @param isLinked    是否新建LinkedList
     * @param collections 集合
     * @return List对象
     */
    @SafeVarargs
    static <T> List<T> list(boolean isLinked, Collection<T>... collections) {
        List<T> result = list(isLinked);
        if (null != collections) {
            for (Collection<T> collection : collections) {
                result.addAll(collection);
            }
        }
        return result;
    }

    /**
     * 新建一个List<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>       集合元素类型
     * @param isLinked  是否新建LinkedList
     * @param iterables {@link Iterable}
     * @return List对象
     */
    @SafeVarargs
    static <T> List<T> list(boolean isLinked, Iterable<T>... iterables) {
        List<T> result = list(isLinked);
        if (null != iterables) {
            for (Iterable<T> iterable : iterables) {
                for (T t : iterable) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    /**
     * 新建一个List<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>       集合元素类型
     * @param isLinked  是否新建LinkedList
     * @param iterators {@link Iterator}
     * @return ArrayList对象
     */
    @SafeVarargs
    static <T> List<T> list(boolean isLinked, Iterator<T>... iterators) {
        final List<T> list = list(isLinked);
        if (null != iterators) {
            for (Iterator<T> iterator : iterators) {
                while (iterator.hasNext()) {
                    list.add(iterator.next());
                }
            }
        }
        return list;
    }

    /**
     * 新建一个List<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>          集合元素类型
     * @param isLinked     是否新建LinkedList
     * @param enumerations {@link Enumeration}
     * @return ArrayList对象
     */
    @SafeVarargs
    static <T> List<T> list(boolean isLinked, Enumeration<T>... enumerations) {
        final List<T> list = list(isLinked);
        if (null != enumerations) {
            for (Enumeration<T> enumeration : enumerations) {
                while (enumeration.hasMoreElements()) {
                    list.add(enumeration.nextElement());
                }
            }
        }
        return list;
    }

    /**
     * 新建一个CopyOnWriteArrayList
     *
     * @param <T>         集合元素类型
     * @param collections 集合
     * @return {@link CopyOnWriteArrayList}
     */
    @SafeVarargs
    static <T> CopyOnWriteArrayList<T> copyOnWriteArrayList(Collection<T>... collections) {
        if (collections == null || collections.length == 0) {
            return new CopyOnWriteArrayList<>();
        } else if (collections.length == 1) {
            return new CopyOnWriteArrayList<>(collections[0]);
        } else {
            CopyOnWriteArrayList<T> result = new CopyOnWriteArrayList<>();
            if (null != collections) {
                for (Collection<T> collection : collections) {
                    result.addAll(collection);
                }
            }
            return result;
        }
    }

    /**
     * 新建一个空List
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @return List对象
     */
    static <T> Set<T> set(boolean isLinked) {
        return isLinked ? new LinkedHashSet<>() : new HashSet<>();
    }

    /**
     * 新建一个Set
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedHashSet
     * @param values   数组
     * @return Set对象
     */
    @SafeVarargs
    static <T> Set<T> set(boolean isLinked, T... values) {
        Set<T> result = set(isLinked);
        if (null != values) {
            Collections.addAll(result, values);
        }
        return result;
    }

    /**
     * 新建一个Set
     *
     * @param <T>         集合元素类型
     * @param isLinked    是否新建LinkedHashSet
     * @param collections 集合
     * @return Set对象
     */
    @SafeVarargs
    static <T> Set<T> set(boolean isLinked, Collection<T>... collections) {
        Set<T> result = set(isLinked);
        if (null != collections) {
            for (Collection<T> collection : collections) {
                result.addAll(collection);
            }
        }
        return result;
    }

    /**
     * 新建一个Set<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>       集合元素类型
     * @param isLinked  是否新建LinkedHashSet
     * @param iterables {@link Iterable}
     * @return Set对象
     */
    @SafeVarargs
    static <T> Set<T> set(boolean isLinked, Iterable<T>... iterables) {
        Set<T> result = set(isLinked);
        if (null != iterables) {
            for (Iterable<T> iterable : iterables) {
                for (T t : iterable) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    /**
     * 新建一个Set<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>       集合元素类型
     * @param isLinked  是否新建LinkedHashSet
     * @param iterators {@link Iterator}
     * @return Set对象
     */
    @SafeVarargs
    static <T> Set<T> set(boolean isLinked, Iterator<T>... iterators) {
        Set<T> result = set(isLinked);
        if (null != iterators) {
            for (Iterator<T> iterator : iterators) {
                while (iterator.hasNext()) {
                    result.add(iterator.next());
                }
            }
        }
        return result;
    }

    /**
     * 新建一个Set<br>
     * 提供的参数为null时返回空{@link LinkedHashSet}
     *
     * @param <T>          集合元素类型
     * @param isLinked     是否新建LinkedHashSet
     * @param enumerations {@link Enumeration}
     * @return Set对象
     */
    @SafeVarargs
    static <T> Set<T> set(boolean isLinked, Enumeration<T>... enumerations) {
        Set<T> result = set(isLinked);
        if (null != enumerations) {
            for (Enumeration<T> enumeration : enumerations) {
                while (enumeration.hasMoreElements()) {
                    result.add(enumeration.nextElement());
                }
            }
        }
        return result;
    }

    /**
     * 新建{@link BlockingQueue}<br>
     * 在队列为空时，获取元素的线程会等待队列变为非空。当队列满时，存储元素的线程会等待队列可用。
     *
     * @param <T>      集合类型
     * @param isLinked 是否为链表形式
     * @param capacity 容量
     * @return {@link BlockingQueue}
     */
    static <T> BlockingQueue<T> blockingQueue(boolean isLinked, int capacity) {
        BlockingQueue<T> queue;
        if (isLinked) {
            queue = new LinkedBlockingDeque<>(capacity);
        } else {
            queue = new ArrayBlockingQueue<>(capacity);
        }
        return queue;
    }

    /**
     * {@link ByteBuffer} 转byte数组
     *
     * @param input {@link ByteBuffer}
     * @return byte数组
     */
    static byte[] bytes(@Nonnull ByteBuffer input) {
        if (input.hasArray()) {
            return Arrays.copyOfRange(input.array(), input.position(), input.limit());
        } else {
            int oldPosition = input.position();
            input.position(0);
            int size = input.limit();
            byte[] buffers = new byte[size];
            input.get(buffers);
            input.position(oldPosition);
            return buffers;
        }
    }

    static byte[] bytes(char input) {
        return new byte[]{(byte) (input >> 8 & 0xFF), (byte) (input & 0xFF)};
    }

    static byte[] bytes(@Nonnull String input, Charsets charsets) {
        if (input == null) {
            return PoolOfArray.EMPTY_BYTE_ARRAY;
        }
        byte[] bytes;
        if (charsets == null) {
            bytes = input.getBytes();
        } else {
            Charset charset = charsets.get();
            if (charset == null) {
                bytes = input.getBytes();
            } else {
                bytes = input.getBytes(charset);
            }
        }
        return bytes;
    }

    /**
     * 从集合中创建数组
     *
     * @param collection    集合
     * @param componentType 数组类型
     * @param <T>           元素类型
     * @return 返回数组
     */
    static <T> T[] array(@Nonnull Collection<T> collection, @Nonnull Class<?> componentType) {
        return collection.toArray(array(componentType));
    }

    /**
     * 新建一个空数组
     *
     * @param size 大小
     * @return 空数组
     */
    static Object[] array(int size) {
        return array(Object.class, size);
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @return 空数组
     */
    static <T> T[] array(@Nonnull Class<?> componentType) {
        return array(componentType, 0);
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param size          大小
     * @return 空数组
     */
    static <T> T[] array(@Nonnull Class<?> componentType, int size) {
        return (T[]) Array.newInstance(componentType, size);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Map
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 返回集合的索引映射Map
     *
     * @param collection 集合
     * @param <E>        元素类型
     * @return {@code <E> Parametric<Integer, E>}
     */
    static <E> Map<Integer, E> indexMap(boolean isLinked, Collection<E> collection) {
        Map<Integer, E> result = New.map(isLinked, collection.size());
        Iterator<E> iterator = collection.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            result.put(i, iterator.next());
        }
        return result;
    }

    @Nonnull
    static <K, V> TreeMap<K, V> treeMap(Comparator<? super K> comparator) {
        return new TreeMap<>(comparator);
    }

    @Nonnull
    static <K, V> TreeMap<K, V> treeMap(Map<K, V> map, Comparator<? super K> comparator) {
        final TreeMap<K, V> treeMap = new TreeMap<>(comparator);
        if (!Whether.empty(map)) {
            treeMap.putAll(map);
        }
        return treeMap;
    }

    @Nonnull
    static <K, V> Map<K, V> newIdentityMap(int size) {
        return new IdentityHashMap<>(size);
    }

    @Nonnull
    static <K, V> ConcurrentHashMap<K, V> concurrentHashMap() {
        return new ConcurrentHashMap<>(16);
    }

    @Nonnull
    static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(int size) {
        final int initCapacity = size <= 0 ? 16 : size;
        return new ConcurrentHashMap<>(initCapacity);
    }

    @Nonnull
    static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(Map<K, V> map) {
        if (Whether.empty(map)) {
            return new ConcurrentHashMap<>(16);
        }
        return new ConcurrentHashMap<>(map);
    }

    @Nonnull
    static <K, V> HashMap<K, V> map() {
        return New.map(false, null, null);
    }

    @Nonnull
    static <K, V> HashMap<K, V> map(int size) {
        return New.map(false, null, null);
    }

    @Nonnull
    static <K, V> HashMap<K, V> map(boolean isLinked) {
        return New.map(isLinked, null, null);
    }

    @Nonnull
    static <K, V> HashMap<K, V> map(boolean isLinked, int initialCapacity) {
        return New.map(isLinked, initialCapacity, null);
    }

    @Nonnull
    static <K, V> HashMap<K, V> map(boolean isLinked, Integer initialCapacity, Float loadFactor) {
        initialCapacity = Safe.value(initialCapacity, 16);
        loadFactor = Safe.value(loadFactor, 0.75f);
        return isLinked ? new LinkedHashMap<>(initialCapacity, loadFactor) : new HashMap<>(initialCapacity, loadFactor);
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked, K k, V v) {
        Map<K, V> map = New.map(isLinked, 1, 1f);
        map.put(k, v);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked, K k1, V v1, K k2, V v2) {
        Map<K, V> map = New.map(isLinked, 2, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked, K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = New.map(isLinked, 3, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K, V> map = New.map(isLinked, 4, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K, V> map = New.map(isLinked, 5, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        Map<K, V> map = New.map(isLinked, 6, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        Map<K, V> map = New.map(isLinked, 7, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1,
                                K k2, V v2,
                                K k3, V v3,
                                K k4, V v4,
                                K k5, V v5,
                                K k6, V v6,
                                K k7, V v7,
                                K k8, V v8) {
        Map<K, V> map = New.map(isLinked, 8, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1,
                                K k2, V v2,
                                K k3, V v3,
                                K k4, V v4,
                                K k5, V v5,
                                K k6, V v6,
                                K k7, V v7,
                                K k8, V v8,
                                K k9, V v9) {
        Map<K, V> map = New.map(isLinked, 9, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1,
                                K k2, V v2,
                                K k3, V v3,
                                K k4, V v4,
                                K k5, V v5,
                                K k6, V v6,
                                K k7, V v7,
                                K k8, V v8,
                                K k9, V v9,
                                K k10, V v10) {
        Map<K, V> map = New.map(isLinked, 10, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1,
                                K k2, V v2,
                                K k3, V v3,
                                K k4, V v4,
                                K k5, V v5,
                                K k6, V v6,
                                K k7, V v7,
                                K k8, V v8,
                                K k9, V v9,
                                K k10, V v10,
                                K k11, V v11) {
        Map<K, V> map = New.map(isLinked, 11, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        map.put(k11, v11);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1,
                                K k2, V v2,
                                K k3, V v3,
                                K k4, V v4,
                                K k5, V v5,
                                K k6, V v6,
                                K k7, V v7,
                                K k8, V v8,
                                K k9, V v9,
                                K k10, V v10,
                                K k11, V v11,
                                K k12, V v12) {
        Map<K, V> map = New.map(isLinked, 11, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        map.put(k11, v11);
        map.put(k12, v12);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1,
                                K k2, V v2,
                                K k3, V v3,
                                K k4, V v4,
                                K k5, V v5,
                                K k6, V v6,
                                K k7, V v7,
                                K k8, V v8,
                                K k9, V v9,
                                K k10, V v10,
                                K k11, V v11,
                                K k12, V v12,
                                K k13, V v13) {
        Map<K, V> map = New.map(isLinked, 13, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        map.put(k11, v11);
        map.put(k12, v12);
        map.put(k13, v13);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1,
                                K k2, V v2,
                                K k3, V v3,
                                K k4, V v4,
                                K k5, V v5,
                                K k6, V v6,
                                K k7, V v7,
                                K k8, V v8,
                                K k9, V v9,
                                K k10, V v10,
                                K k11, V v11,
                                K k12, V v12,
                                K k13, V v13,
                                K k14, V v14) {
        Map<K, V> map = New.map(isLinked, 14, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        map.put(k11, v11);
        map.put(k12, v12);
        map.put(k13, v13);
        map.put(k14, v14);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1,
                                K k2, V v2,
                                K k3, V v3,
                                K k4, V v4,
                                K k5, V v5,
                                K k6, V v6,
                                K k7, V v7,
                                K k8, V v8,
                                K k9, V v9,
                                K k10, V v10,
                                K k11, V v11,
                                K k12, V v12,
                                K k13, V v13,
                                K k14, V v14,
                                K k15, V v15) {
        Map<K, V> map = New.map(isLinked, 15, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        map.put(k11, v11);
        map.put(k12, v12);
        map.put(k13, v13);
        map.put(k14, v14);
        map.put(k15, v15);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> map(boolean isLinked,
                                K k1, V v1,
                                K k2, V v2,
                                K k3, V v3,
                                K k4, V v4,
                                K k5, V v5,
                                K k6, V v6,
                                K k7, V v7,
                                K k8, V v8,
                                K k9, V v9,
                                K k10, V v10,
                                K k11, V v11,
                                K k12, V v12,
                                K k13, V v13,
                                K k14, V v14,
                                K k15, V v15,
                                K k16, V v16) {
        Map<K, V> map = New.map(isLinked, 16, 1f);
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        map.put(k9, v9);
        map.put(k10, v10);
        map.put(k11, v11);
        map.put(k12, v12);
        map.put(k13, v13);
        map.put(k14, v14);
        map.put(k15, v15);
        map.put(k16, v16);
        return map;
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(K k, V v) {
        return Collections.unmodifiableMap(New.map(true, k, v));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(K k1, V v1, K k2, V v2) {
        return Collections.unmodifiableMap(New.map(true, k1, v1, k2, v2));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return Collections.unmodifiableMap(New.map(true, k1, v1, k2, v2, k3, v3));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return Collections.unmodifiableMap(New.map(true, k1, v1, k2, v2, k3, v3, k4, v4));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return Collections.unmodifiableMap(New.map(true, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return Collections.unmodifiableMap(New.map(true, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return Collections.unmodifiableMap(
                New.map(true, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8) {
        Map<K, V> map = new HashMap<>(8, 1f);
        return Collections.unmodifiableMap(
                New.map(true, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9) {
        return Collections.unmodifiableMap(
                New.map(true, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9,
            K k10, V v10) {
        return Collections.unmodifiableMap(
                New.map(true, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9,
            K k10, V v10,
            K k11, V v11) {
        return Collections.unmodifiableMap(
                New.map(true,
                        k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11,
                        v11));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9,
            K k10, V v10,
            K k11, V v11,
            K k12, V v12) {
        return Collections.unmodifiableMap(
                New.map(true,
                        k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11,
                        v11, k12, v12));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9,
            K k10, V v10,
            K k11, V v11,
            K k12, V v12,
            K k13, V v13) {
        return Collections.unmodifiableMap(
                New.map(true,
                        k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10, k11,
                        v11, k13, v13));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9,
            K k10, V v10,
            K k11, V v11,
            K k12, V v12,
            K k13, V v13,
            K k14, V v14) {
        return Collections.unmodifiableMap(
                New.map(true,
                        k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10,
                        k11, v11, k13, v13, k14, v14));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9,
            K k10, V v10,
            K k11, V v11,
            K k12, V v12,
            K k13, V v13,
            K k14, V v14,
            K k15, V v15) {
        return Collections.unmodifiableMap(
                New.map(true, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10,
                        k11, v11, k13, v13, k14, v14, k15, v15));
    }

    @Nonnull
    static <K, V> Map<K, V> unmodifiableMap(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9,
            K k10, V v10,
            K k11, V v11,
            K k12, V v12,
            K k13, V v13,
            K k14, V v14,
            K k15, V v15,
            K k16, V v16) {
        return Collections.unmodifiableMap(
                New.map(true, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10,
                        k11, v11, k13, v13, k14, v14, k15, v15, k16, v16));
    }

    @Nonnull
    static <E extends Enum<E>> Map<String, E> ofMapFromEnum(
            @Nonnull Class<E> enumClass, @Nonnull Map<String, E> container) {
        return ofMapFromEnum(enumClass, container, null);
    }

    /**
     * 从枚举类构造{@code ReadOnlyCaseInsensitiveMap}
     *
     * @param enumClass    枚举类
     * @param keysSupplier 键值提供器
     * @param <E>          枚举类型
     * @return {@code ReadOnlyCaseInsensitiveMap}
     */
    @Nonnull
    static <E extends Enum<E>> Map<String, E> ofMapFromEnum(
            @Nonnull Class<E> enumClass,
            @Nonnull Map<String, E> container,
            @Nullable FunctionThrow<E, Iterable<String>, RuntimeException> keysSupplier) {
        if (enumClass.isEnum()) {
            E[] enumConstants = enumClass.getEnumConstants();
            for (E enumConstant : enumConstants) {
                container.put(enumConstant.name(), enumConstant);
            }
            if (keysSupplier != null) {
                for (E enumConstant : enumConstants) {
                    Iterable<String> keys = keysSupplier.apply(enumConstant);
                    if (keys != null) {
                        for (String key : keys) {
                            if (!container.containsKey(key)) {
                                container.put(key, enumConstant);
                            }
                        }
                    }
                }
            }
        }
        return container;
    }

    /**
     * 将Entry集合转换为HashMap
     *
     * @param resultMap 结果Map，通过传入map对象决定结果的Map类型
     * @param entryIter entry集合
     * @param <K>       键类型
     * @param <V>       值类型
     * @return Map
     */
    static <K, V> Map<K, V> map(@Nonnull Map<K, V> resultMap, @Nonnull Iterable<Map.Entry<K, V>> entryIter) {
        for (Map.Entry<K, V> entry : entryIter) {
            resultMap.put(entry.getKey(), entry.getValue());
        }
        return resultMap;
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param resultMap 结果Map，通过传入map对象决定结果的Map类型
     * @param keys      键列表
     * @param values    值列表
     * @param <K>       键类型
     * @param <V>       值类型
     * @return Map
     */
    static <K, V> Map<K, V> map(@Nonnull Map<K, V> resultMap, @Nonnull Iterable<K> keys, @Nonnull Iterable<V> values) {
        return map(resultMap, keys.iterator(), values.iterator());
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param resultMap 结果Map，通过传入map对象决定结果的Map类型
     * @param keys      键列表
     * @param values    值列表
     * @param <K>       键类型
     * @param <V>       值类型
     * @return Map
     */
    static <K, V> Map<K, V> map(
            @Nonnull Map<K, V> resultMap, @Nonnull Iterator<K> keys, @Nonnull Iterator<V> values) {
        while (keys.hasNext()) {
            resultMap.put(keys.next(), (null != values && values.hasNext()) ? values.next() : null);
        }
        return resultMap;
    }


    /**
     * 将列表转成HashMap
     *
     * @param resultMap 结果Map，通过传入map对象决定结果的Map类型
     * @param iterable  值列表
     * @param keyMapper Map的键映射
     * @param <K>       键类型
     * @param <V>       值类型
     * @return Map
     */
    static <K, V> Map<K, V> map(
            @Nonnull Map<K, V> resultMap, @Nonnull Iterable<V> iterable, @Nonnull Function<V, K> keyMapper) {
        return map(resultMap, iterable, keyMapper, v -> v);
    }

    /**
     * 将列表转成Map
     *
     * @param resultMap   结果Map，通过传入map对象决定结果的Map类型
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return Map
     */
    static <T, K, V> Map<K, V> map(
            @Nonnull Map<K, V> resultMap,
            @Nonnull Iterable<T> iterable,
            @Nonnull Function<T, K> keyMapper,
            @Nonnull Function<T, V> valueMapper) {
        for (T value : iterable) {
            resultMap.put(keyMapper.apply(value), valueMapper.apply(value));
        }

        return resultMap;
    }

    /**
     * 将列表转成值为List的HashMap
     *
     * @param resultMap 结果Map，可自定义结果Map类型
     * @param iterable  值列表
     * @param keyMapper Map的键映射
     * @param <K>       键类型
     * @param <V>       值类型
     * @return Map
     */
    static <K, V> Map<K, List<V>> listMap(
            @Nonnull Map<K, List<V>> resultMap, @Nonnull Iterable<V> iterable, @Nonnull Function<V, K> keyMapper) {
        return listMap(resultMap, iterable, keyMapper, v -> v);
    }

    /**
     * 将列表转成值为List的HashMap
     *
     * @param resultMap   结果Map，可自定义结果Map类型
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map中List的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return Map
     */
    static <T, K, V> Map<K, List<V>> listMap(
            @Nonnull Map<K, List<V>> resultMap,
            @Nonnull Iterable<T> iterable,
            @Nonnull Function<T, K> keyMapper,
            @Nonnull Function<T, V> valueMapper) {
        for (T value : iterable) {
            resultMap
                    .computeIfAbsent(keyMapper.apply(value), k -> new ArrayList<>())
                    .add(valueMapper.apply(value));
        }

        return resultMap;
    }

    static int[] arr(int... values) {
        return values;
    }
}
