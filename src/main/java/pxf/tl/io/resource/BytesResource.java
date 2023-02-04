package pxf.tl.io.resource;


import pxf.tl.api.Charsets;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.help.New;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

/**
 * 基于byte[]的资源获取器<br>
 * 注意：此对象中getUrl方法始终返回null
 *
 * @author potatoxf
 */
public class BytesResource implements Resource, Serializable {
    private static final long serialVersionUID = 1L;

    private final byte[] bytes;
    private final String name;

    /**
     * 构造
     *
     * @param bytes 字节数组
     */
    public BytesResource(byte[] bytes) {
        this(bytes, null);
    }

    /**
     * 构造
     *
     * @param bytes 字节数组
     * @param name  资源名称
     */
    public BytesResource(byte[] bytes, String name) {
        this.bytes = bytes;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public String readStr(Charsets charsets) throws IORuntimeException {
        return New.string(this.bytes, charsets);
    }

    @Override
    public byte[] readBytes() throws IORuntimeException {
        return this.bytes;
    }
}
