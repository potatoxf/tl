package pxf.tl.api;


import pxf.tl.collection.map.CaseInsensitiveMap;
import pxf.tl.collection.map.UnModificationMap;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.exception.NoSuchConstantException;
import pxf.tl.exception.UnsupportedException;
import pxf.tl.help.New;
import pxf.tl.util.ToolIO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.util.*;

/**
 * @author potatoxf
 */
public enum Charsets {
    /**
     * 字符集
     */
    ISO_8859_1(StandardCharsets.ISO_8859_1, "ISO_8859_1"),
    US_ASCII(StandardCharsets.US_ASCII, "ASCII"),
    UTF_8(StandardCharsets.UTF_8, "UTF-8", "UTF8"),
    UTF_16(StandardCharsets.UTF_16, "UTF-16", "UTF16"),
    UTF_16BE(StandardCharsets.UTF_16BE, "UTF-16BE", "UTF16BE"),
    UTF_16LE(StandardCharsets.UTF_16LE, "UTF-16LE", "UTF16LE"),
    UTF_32BE(getCharset("UTF_32BE"), "UTF-32BE", "UTF32BE"),
    UTF_32LE(getCharset("UTF_32LE"), "UTF-32LE", "UTF32LE"),
    UNICODE(getCharset("UNICODE")),
    BIG5(getCharset("BIG5")),
    GB2312(getCharset("GB2312")),
    GBK(getCharset("GBK")),
    GB18030(getCharset("GB18030"));
    private static final Map<String, Charsets> CACHE =
            New.ofMapFromEnum(
                    Charsets.class,
                    new UnModificationMap<>(new CaseInsensitiveMap<>()),
                    charsetToken -> charsetToken.alias);
    /**
     * 默认的参与测试的编码
     */
    private static final Charset[] DEFAULT_CHARSETS = Arrays.stream(Charsets.values())
            .map(Charsets::get).filter(Objects::nonNull).toArray(Charset[]::new);
    private final Charset charset;
    private final Set<String> alias;

    Charsets(String name, String... alias) {
        this(getCharset(name), alias);
    }

    Charsets(Charset charset, String... alias) {
        this.charset = charset;
        if (charset == null) {
            this.alias = Set.of(alias);
        } else if (alias.length == 0) {
            this.alias = Set.of(charset.name());
        } else {
            Set<String> set = New.set(true, charset.name());
            Collections.addAll(set, alias);
            this.alias = Collections.unmodifiableSet(set);
        }
    }

    /**
     * 通过字符串解析{@code CharsetToken}
     *
     * @param charsetName charset名称
     * @return {@code CharsetToken}
     */
    public static Charsets getCharsets(@Nonnull String charsetName, @Nonnull Charsets defaultCharsets) {
        try {
            return parseCharsets(charsetName);
        } catch (UnsupportedException e) {
            return defaultCharsets;
        }
    }

    /**
     * 通过字符串解析{@code CharsetToken}
     *
     * @param charsetName charset名称
     * @return {@code CharsetToken}
     */
    public static Charset getCharset(@Nonnull String charsetName, @Nonnull Charset defaultCharset) {
        try {
            return parseCharset(charsetName);
        } catch (UnsupportedException e) {
            return defaultCharset;
        }
    }

    /**
     * 通过字符串解析{@code CharsetToken}
     *
     * @param charsetName charset名称
     * @return {@code CharsetToken}
     */
    public static Charset getCharset(@Nonnull String charsetName, @Nonnull Charsets defaultCharset) {
        try {
            return parseCharset(charsetName);
        } catch (UnsupportedException e) {
            return defaultCharset.charset;
        }
    }

    /**
     * 通过字符串解析{@code CharsetToken}
     *
     * @param charsetName charset名称
     * @return {@code CharsetToken}
     */
    public static Charsets parseCharsets(@Nonnull String charsetName) {
        Charsets charsets = CACHE.get(charsetName);
        if (charsets == null) {
            throw new UnsupportedException("Charset not found [" + charsetName + "]");
        }
        return charsets;
    }

    /**
     * 通过字符串解析{@code CharsetToken}
     *
     * @param charsetName charset名称
     * @return {@code CharsetToken}
     * @throws UnsupportedException 如果未能解析成功返回次异常
     */
    public static Charset parseCharset(@Nonnull String charsetName) {
        Charsets charsets = CACHE.get(charsetName);
        if (charsets == null) {
            Charset charset = getCharset(charsetName);
            if (charset == null) {
                throw new UnsupportedException("Charset not found [" + charsetName + "]");
            } else {
                return charset;
            }
        } else {
            return charsets.charset;
        }
    }

    @Nullable
    private static Charset getCharset(@Nonnull String charsetName) {
        try {
            return Charset.forName(charsetName);
        } catch (UnsupportedCharsetException ignored) {
        }
        return null;
    }


    /**
     * 转换字符串的字符集编码
     *
     * @param source      字符串
     * @param srcCharset  源字符集，默认ISO-8859-1
     * @param destCharset 目标字符集，默认UTF-8
     * @return 转换后的字符集
     */
    @Nonnull
    public static String convert(@Nonnull String source, @Nonnull String srcCharset, @Nonnull String destCharset) throws NoSuchConstantException {
        return convert(source, parseCharsets(srcCharset), parseCharsets(destCharset));
    }

    /**
     * 转换字符串的字符集编码<br>
     * 当以错误的编码读取为字符串时，打印字符串将出现乱码。<br>
     * 此方法用于纠正因读取使用编码错误导致的乱码问题。<br>
     * 例如，在Servlet请求中客户端用GBK编码了请求参数，我们使用UTF-8读取到的是乱码，此时，使用此方法即可还原原编码的内容
     *
     * <pre>
     * 客户端 -》 GBK编码 -》 Servlet容器 -》 UTF-8解码 -》 乱码
     * 乱码 -》 UTF-8编码 -》 GBK解码 -》 正确内容
     * </pre>
     *
     * @param source       字符串
     * @param srcCharsets  源字符集，默认ISO-8859-1
     * @param destCharsets 目标字符集，默认UTF-8
     * @return 转换后的字符集
     */
    @Nonnull
    public static String convert(@Nonnull String source, @Nonnull Charsets srcCharsets, @Nonnull Charsets destCharsets) {
        if (srcCharsets == destCharsets) {
            return source;
        } else {
            return new String(source.getBytes(srcCharsets.charset), destCharsets.charset);
        }
    }

    /**
     * 系统默认字符集编码
     *
     * @return 系统字符集编码
     */
    @Nonnull
    public static Charsets defaultCharsets() {
        return UTF_8;
    }

    /**
     * 系统默认字符集编码
     *
     * @return 系统字符集编码
     */
    @Nonnull
    public static Charset systemCharset() {
        return Charset.defaultCharset();
    }

    /**
     * 探测编码<br>
     * 注意：此方法会读取流的一部分，然后关闭流，如重复使用流，请使用使用支持reset方法的流
     *
     * @param in       流，使用后关闭此流
     * @param charsets 需要测试用的编码，null或空使用默认的编码数组
     * @return 编码
     */
    @Nonnull
    public static Charset systemCharset(@Nonnull InputStream in, Charset... charsets) {
        return detect(in, charsets);
    }

    /**
     * 探测编码<br>
     * 注意：此方法会读取流的一部分，然后关闭流，如重复使用流，请使用使用支持reset方法的流
     *
     * @param bufferSize 自定义缓存大小，即每次检查的长度
     * @param in         流，使用后关闭此流
     * @param charsets   需要测试用的编码，null或空使用默认的编码数组
     * @return 编码
     */
    public static Charset systemCharset(int bufferSize, InputStream in, Charset... charsets) {
        return detect(bufferSize, in, charsets);
    }

    /**
     * 探测编码<br>
     * 注意：此方法会读取流的一部分，然后关闭流，如重复使用流，请使用支持reset方法的流
     *
     * @param in       流，使用后关闭此流
     * @param charsets 需要测试用的编码，null或空使用默认的编码数组
     * @return 编码
     */
    public static Charset detect(InputStream in, Charset... charsets) {
        return detect(JavaEnvironment.DEFAULT_BUFFER_SIZE, in, charsets);
    }

    /**
     * 探测编码<br>
     * 注意：此方法会读取流的一部分，然后关闭流，如重复使用流，请使用支持reset方法的流
     *
     * @param bufferSize 自定义缓存大小，即每次检查的长度
     * @param in         流，使用后关闭此流
     * @param charsets   需要测试用的编码，null或空使用默认的编码数组
     * @return 编码
     */
    public static Charset detect(int bufferSize, @Nonnull InputStream in, @Nonnull Charset... charsets) {
        List<Charset> charsetList = new ArrayList<>(DEFAULT_CHARSETS.length + charsets.length);
        Collections.addAll(charsetList, charsets);
        Collections.addAll(charsetList, DEFAULT_CHARSETS);

        final byte[] buffer = new byte[bufferSize];
        try {
            while (in.read(buffer) > -1) {
                for (Charset charset : charsetList) {
                    final CharsetDecoder decoder = charset.newDecoder();
                    if (identify(buffer, decoder)) {
                        return charset;
                    }
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            ToolIO.closes(in);
            ToolIO.closes(in);
        }
        return null;
    }

    /**
     * 通过try的方式测试指定bytes是否可以被解码，从而判断是否为指定编码
     *
     * @param bytes   测试的bytes
     * @param decoder 解码器
     * @return 是否是指定编码
     */
    private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
        try {
            decoder.decode(ByteBuffer.wrap(bytes));
        } catch (CharacterCodingException e) {
            return false;
        }
        return true;
    }

    public Charset get() {
        return charset;
    }

    @Override
    public String toString() {
        return charset.name();
    }
}
