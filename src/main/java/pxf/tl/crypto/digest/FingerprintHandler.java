package pxf.tl.crypto.digest;

import pxf.tl.api.Charsets;
import pxf.tl.help.New;
import pxf.tlx.codec.impl.HexCoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * 指纹处理器
 *
 * @author potatoxf
 */
public interface FingerprintHandler {

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    byte[] sign(byte[] data);

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    byte[] sign(ByteBuffer data);

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    byte[] sign(InputStream data) throws IOException;

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    byte[] sign(FileChannel data) throws IOException;

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default byte[] sign(CharSequence data) {
        return sign(data, Charsets.defaultCharsets());
    }

    /**
     * 生存指纹
     *
     * @param data     数据
     * @param charsets 字符集
     * @return 返回指纹数据
     */
    default byte[] sign(CharSequence data, Charsets charsets) {
        return sign(New.bytes(data.toString(), charsets));
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default String signHex(ByteBuffer data) {
        return HexCoder.INSTANCE.encodeFromTarget(sign(data));
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default String signHex(byte[] data) {
        return HexCoder.INSTANCE.encodeFromTarget(sign(data));
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default String signHex(InputStream data) throws IOException {
        return HexCoder.INSTANCE.encodeFromTarget(sign(data));
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default String signHex(FileChannel data) throws IOException {
        return HexCoder.INSTANCE.encodeFromTarget(sign(data));
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default String signHex(Path data) throws IOException {
        return HexCoder.INSTANCE.encodeFromTarget(sign(data));
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default byte[] sign(Path data) throws IOException {
        return sign(data.toFile());
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default byte[] sign(File data) throws IOException {
        return sign(new FileInputStream(data).getChannel());
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default String signHex(File data) throws IOException {
        return HexCoder.INSTANCE.encodeFromTarget(sign(data));
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default String signHex(RandomAccessFile data) throws IOException {
        return HexCoder.INSTANCE.encodeFromTarget(sign(data));
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default byte[] sign(RandomAccessFile data) throws IOException {
        return sign(data.getChannel());
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    default String signHex(CharSequence data) {
        return signHex(data, Charsets.defaultCharsets());
    }

    /**
     * 生存指纹
     *
     * @param data     数据
     * @param charsets 字符集
     * @return 返回指纹数据
     */
    default String signHex(CharSequence data, Charsets charsets) {
        return HexCoder.INSTANCE.encodeFromTarget(sign(data, charsets));
    }
}
