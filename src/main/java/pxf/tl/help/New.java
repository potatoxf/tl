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
     * ???URL????????????
     *
     * @param url {@link URL}
     * @return InputStream???
     */
    static InputStream inputStream(@Nonnull URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * ???URL???URI
     *
     * @param url URL
     * @return URI
     */
    @Nonnull
    static URI uri(@Nonnull URL url) {
        return uri(url, null);
    }

    /**
     * ???URL???URI
     *
     * @param url      URL
     * @param charsets ?????????????????????????????????????????????UTF-8?????????
     * @return URI
     */
    @Nonnull
    static URI uri(@Nonnull URL url, @Nullable Charsets charsets) {
        return uri(url.toString(), charsets);
    }


    /**
     * ???????????????URI
     *
     * @param location ???????????????
     * @return URI
     */
    @Nonnull
    static URI uri(@Nonnull String location) {
        return uri(location, null);
    }

    /**
     * ???????????????URI
     *
     * @param location ???????????????
     * @param charsets ?????????????????????????????????????????????UTF-8?????????
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
                for (int i = 1; i <= 3; i++) { // ????????????????????????????????? i ?????? 1 ~ 3 ??????
                    // ???????????????????????????????????? false?????????????????????????????????????????????????????????????????????????????????
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
                for (int i = 1; i <= 3; i++) { // ????????????????????????????????? i ?????? 1 ~ 3 ??????
                    // ???????????????????????????????????? false?????????????????????????????????????????????????????????????????????????????????
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
     * ?????????{@link InputStream} ?????????{@link InputStream#available()}?????????????????????<br>
     * ???Socket????????????????????????????????????????????????{@link InputStream#available()}???????????????{@code 0}<br>
     * ?????????????????????????????????{@link InputStream#read()}??????????????????????????????????????????????????????????????????{@link
     * InputStream#available()}?????????????????????<br>
     * ???????????????????????????????????????????????????????????????????????? {@link InputStream#available()} ????????????????????????????????????????????????????????????<br>
     * ????????????????????????????????????
     *
     * <ul>
     *   <li>FileInputStream ????????????????????????????????????available??????????????????
     *   <li>??????InputStream ??????PushbackInputStream
     * </ul>
     *
     * @param in ???????????????
     * @return ???????????????????????????{@link PushbackInputStream}
     */
    @Nonnull
    static InputStream availableStream(@Nonnull InputStream in) {
        if (in instanceof FileInputStream) {
            // FileInputStream????????????available?????????
            return in;
        }

        final PushbackInputStream pushbackInputStream = pushbackInputStream(in, 1);
        try {
            final int available = pushbackInputStream.available();
            if (available <= 0) {
                // ?????????????????????????????????????????????
                int b = pushbackInputStream.read();
                pushbackInputStream.unread(b);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }

        return pushbackInputStream;
    }

    /**
     * ???{@link InputStream}???????????????mark????????????<br>
     * ???????????????mark???????????????????????????????????????{@link BufferedInputStream} ?????????
     *
     * @param in ???
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
     * ???{@link InputStream}???????????????mark????????????<br>
     * ???????????????mark???????????????????????????????????????{@link BufferedInputStream} ?????????
     *
     * @param reader ???
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
     * ???????????????List
     *
     * @param <T>      ??????????????????
     * @param isLinked ????????????LinkedList
     * @return List??????
     */
    static <T> List<T> list(boolean isLinked) {
        return isLinked ? new LinkedList<>() : new ArrayList<>();
    }

    /**
     * ????????????List
     *
     * @param <T>      ??????????????????
     * @param isLinked ????????????LinkedList
     * @param values   ??????
     * @return List??????
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
     * ????????????List
     *
     * @param <T>         ??????????????????
     * @param isLinked    ????????????LinkedList
     * @param collections ??????
     * @return List??????
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
     * ????????????List<br>
     * ??????????????????null????????????{@link ArrayList}
     *
     * @param <T>       ??????????????????
     * @param isLinked  ????????????LinkedList
     * @param iterables {@link Iterable}
     * @return List??????
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
     * ????????????List<br>
     * ??????????????????null????????????{@link ArrayList}
     *
     * @param <T>       ??????????????????
     * @param isLinked  ????????????LinkedList
     * @param iterators {@link Iterator}
     * @return ArrayList??????
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
     * ????????????List<br>
     * ??????????????????null????????????{@link ArrayList}
     *
     * @param <T>          ??????????????????
     * @param isLinked     ????????????LinkedList
     * @param enumerations {@link Enumeration}
     * @return ArrayList??????
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
     * ????????????CopyOnWriteArrayList
     *
     * @param <T>         ??????????????????
     * @param collections ??????
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
     * ???????????????List
     *
     * @param <T>      ??????????????????
     * @param isLinked ????????????LinkedList
     * @return List??????
     */
    static <T> Set<T> set(boolean isLinked) {
        return isLinked ? new LinkedHashSet<>() : new HashSet<>();
    }

    /**
     * ????????????Set
     *
     * @param <T>      ??????????????????
     * @param isLinked ????????????LinkedHashSet
     * @param values   ??????
     * @return Set??????
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
     * ????????????Set
     *
     * @param <T>         ??????????????????
     * @param isLinked    ????????????LinkedHashSet
     * @param collections ??????
     * @return Set??????
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
     * ????????????Set<br>
     * ??????????????????null????????????{@link ArrayList}
     *
     * @param <T>       ??????????????????
     * @param isLinked  ????????????LinkedHashSet
     * @param iterables {@link Iterable}
     * @return Set??????
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
     * ????????????Set<br>
     * ??????????????????null????????????{@link ArrayList}
     *
     * @param <T>       ??????????????????
     * @param isLinked  ????????????LinkedHashSet
     * @param iterators {@link Iterator}
     * @return Set??????
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
     * ????????????Set<br>
     * ??????????????????null????????????{@link LinkedHashSet}
     *
     * @param <T>          ??????????????????
     * @param isLinked     ????????????LinkedHashSet
     * @param enumerations {@link Enumeration}
     * @return Set??????
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
     * ??????{@link BlockingQueue}<br>
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param <T>      ????????????
     * @param isLinked ?????????????????????
     * @param capacity ??????
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
     * {@link ByteBuffer} ???byte??????
     *
     * @param input {@link ByteBuffer}
     * @return byte??????
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
     * ????????????????????????
     *
     * @param collection    ??????
     * @param componentType ????????????
     * @param <T>           ????????????
     * @return ????????????
     */
    static <T> T[] array(@Nonnull Collection<T> collection, @Nonnull Class<?> componentType) {
        return collection.toArray(array(componentType));
    }

    /**
     * ?????????????????????
     *
     * @param size ??????
     * @return ?????????
     */
    static Object[] array(int size) {
        return array(Object.class, size);
    }

    /**
     * ?????????????????????
     *
     * @param <T>           ??????????????????
     * @param componentType ????????????
     * @return ?????????
     */
    static <T> T[] array(@Nonnull Class<?> componentType) {
        return array(componentType, 0);
    }

    /**
     * ?????????????????????
     *
     * @param <T>           ??????????????????
     * @param componentType ????????????
     * @param size          ??????
     * @return ?????????
     */
    static <T> T[] array(@Nonnull Class<?> componentType, int size) {
        return (T[]) Array.newInstance(componentType, size);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Map
    //------------------------------------------------------------------------------------------------------------------

    /**
     * ???????????????????????????Map
     *
     * @param collection ??????
     * @param <E>        ????????????
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
     * ??????????????????{@code ReadOnlyCaseInsensitiveMap}
     *
     * @param enumClass    ?????????
     * @param keysSupplier ???????????????
     * @param <E>          ????????????
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
     * ???Entry???????????????HashMap
     *
     * @param resultMap ??????Map???????????????map?????????????????????Map??????
     * @param entryIter entry??????
     * @param <K>       ?????????
     * @param <V>       ?????????
     * @return Map
     */
    static <K, V> Map<K, V> map(@Nonnull Map<K, V> resultMap, @Nonnull Iterable<Map.Entry<K, V>> entryIter) {
        for (Map.Entry<K, V> entry : entryIter) {
            resultMap.put(entry.getKey(), entry.getValue());
        }
        return resultMap;
    }

    /**
     * ?????????????????????????????????Map<br>
     * ????????????????????????????????????????????????????????????????????????????????????????????????null?????????<br>
     * ??????????????????????????????????????????
     *
     * @param resultMap ??????Map???????????????map?????????????????????Map??????
     * @param keys      ?????????
     * @param values    ?????????
     * @param <K>       ?????????
     * @param <V>       ?????????
     * @return Map
     */
    static <K, V> Map<K, V> map(@Nonnull Map<K, V> resultMap, @Nonnull Iterable<K> keys, @Nonnull Iterable<V> values) {
        return map(resultMap, keys.iterator(), values.iterator());
    }

    /**
     * ?????????????????????????????????Map<br>
     * ????????????????????????????????????????????????????????????????????????????????????????????????null?????????<br>
     * ??????????????????????????????????????????
     *
     * @param resultMap ??????Map???????????????map?????????????????????Map??????
     * @param keys      ?????????
     * @param values    ?????????
     * @param <K>       ?????????
     * @param <V>       ?????????
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
     * ???????????????HashMap
     *
     * @param resultMap ??????Map???????????????map?????????????????????Map??????
     * @param iterable  ?????????
     * @param keyMapper Map????????????
     * @param <K>       ?????????
     * @param <V>       ?????????
     * @return Map
     */
    static <K, V> Map<K, V> map(
            @Nonnull Map<K, V> resultMap, @Nonnull Iterable<V> iterable, @Nonnull Function<V, K> keyMapper) {
        return map(resultMap, iterable, keyMapper, v -> v);
    }

    /**
     * ???????????????Map
     *
     * @param resultMap   ??????Map???????????????map?????????????????????Map??????
     * @param iterable    ?????????
     * @param keyMapper   Map????????????
     * @param valueMapper Map????????????
     * @param <T>         ???????????????
     * @param <K>         ?????????
     * @param <V>         ?????????
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
     * ?????????????????????List???HashMap
     *
     * @param resultMap ??????Map?????????????????????Map??????
     * @param iterable  ?????????
     * @param keyMapper Map????????????
     * @param <K>       ?????????
     * @param <V>       ?????????
     * @return Map
     */
    static <K, V> Map<K, List<V>> listMap(
            @Nonnull Map<K, List<V>> resultMap, @Nonnull Iterable<V> iterable, @Nonnull Function<V, K> keyMapper) {
        return listMap(resultMap, iterable, keyMapper, v -> v);
    }

    /**
     * ?????????????????????List???HashMap
     *
     * @param resultMap   ??????Map?????????????????????Map??????
     * @param iterable    ?????????
     * @param keyMapper   Map????????????
     * @param valueMapper Map???List????????????
     * @param <T>         ???????????????
     * @param <K>         ?????????
     * @param <V>         ?????????
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
