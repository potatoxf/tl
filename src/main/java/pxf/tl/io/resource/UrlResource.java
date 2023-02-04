package pxf.tl.io.resource;


import pxf.tl.help.New;
import pxf.tl.help.Safe;
import pxf.tl.io.FileUtil;
import pxf.tl.util.ToolURL;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;

/**
 * URL资源访问类
 *
 * @author potatoxf
 */
public class UrlResource implements Resource, Serializable {
    private static final long serialVersionUID = 1L;

    protected URL url;
    protected String name;
    private long lastModified = 0;

    // --------------------------------------------------------------------------------------
    // Constructor start

    /**
     * 构造
     *
     * @param uri URI
     */
    public UrlResource(URI uri) {
        this(ToolURL.url(uri), null);
    }

    /**
     * 构造
     *
     * @param url URL
     */
    public UrlResource(URL url) {
        this(url, null);
    }

    /**
     * 构造
     *
     * @param url  URL，允许为空
     * @param name 资源名称
     */
    public UrlResource(URL url, String name) {
        this.url = url;
        if (null != url && ToolURL.URL_PROTOCOL_FILE.equals(url.getProtocol())) {
            this.lastModified = FileUtil.file(url).lastModified();
        }
        this.name = Safe.value(name, () -> (null != url ? FileUtil.getName(url.getPath()) : null));
    }

    /**
     * 构造
     *
     * @param file 文件路径
     * @deprecated Please use {@link FileResource}
     */
    @Deprecated
    public UrlResource(File file) {
        this.url = ToolURL.getURL(file);
    }
    // --------------------------------------------------------------------------------------
    // Constructor end

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public InputStream getStream() throws NoResourceException {
        if (null == this.url) {
            throw new NoResourceException("Resource URL is null!");
        }
        return New.inputStream(url);
    }

    @Override
    public boolean isModified() {
        // lastModified == 0表示此资源非文件资源
        return (0 != this.lastModified) && this.lastModified != getFile().lastModified();
    }

    /**
     * 获得File
     *
     * @return {@link File}
     */
    public File getFile() {
        return FileUtil.file(this.url);
    }

    /**
     * 返回路径
     *
     * @return 返回URL路径
     */
    @Override
    public String toString() {
        return (null == this.url) ? "null" : this.url.toString();
    }
}
