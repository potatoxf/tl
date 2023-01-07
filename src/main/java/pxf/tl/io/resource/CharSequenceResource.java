package pxf.tl.io.resource;


import pxf.tl.api.Charsets;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.help.New;
import pxf.tl.util.ToolString;

import java.io.*;
import java.net.URL;

/**
 * {@link CharSequence}资源，字符串做为资源
 *
 * @author potatoxf
 */
public class CharSequenceResource implements Resource, Serializable {
    private static final long serialVersionUID = 1L;

    private final CharSequence data;
    private final CharSequence name;
    private final Charsets charsets;

    /**
     * 构造，使用UTF8编码
     *
     * @param data 资源数据
     */
    public CharSequenceResource(CharSequence data) {
        this(data, null);
    }

    /**
     * 构造，使用UTF8编码
     *
     * @param data 资源数据
     * @param name 资源名称
     */
    public CharSequenceResource(CharSequence data, String name) {
        this(data, name, Charsets.UTF_8);
    }

    /**
     * 构造
     *
     * @param data     资源数据
     * @param name     资源名称
     * @param charsets 编码
     */
    public CharSequenceResource(CharSequence data, CharSequence name, Charsets charsets) {
        this.data = data;
        this.name = name;
        this.charsets = charsets;
    }

    @Override
    public String getName() {
        return ToolString.str(this.name);
    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(readBytes());
    }

    @Override
    public BufferedReader getReader(Charsets charsets) {
        return New.bufferedReader(new StringReader(this.data.toString()));
    }

    @Override
    public String readStr(Charsets charsets) throws IORuntimeException {
        return this.data.toString();
    }

    @Override
    public byte[] readBytes() throws IORuntimeException {
        return New.byteArrayInputStream(data.toString(), charsets).readAllBytes();
    }
}
