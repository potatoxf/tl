package pxf.tl.net.url;


import pxf.tl.api.Builder;
import pxf.tl.api.Charsets;
import pxf.tl.api.PoolOfString;
import pxf.tl.help.Assert;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.net.RFC3986;
import pxf.tl.util.ToolString;
import pxf.tl.util.ToolURL;
import pxf.tlx.codec.impl.PercentCoder;

import java.net.*;

/**
 * URL 生成器，格式形如：
 *
 * <pre>
 * [scheme:]scheme-specific-part[#fragment]
 * [scheme:][//authority][path][?query][#fragment]
 * [scheme:][//host:port][path][?query][#fragment]
 * </pre>
 *
 * @author potatoxf
 * @see <a href="https://en.wikipedia.org/wiki/Uniform_Resource_Identifier">Uniform Resource
 * Identifier</a>
 */
public final class UrlBuilder implements Builder<String> {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_SCHEME = "http";

    private static final PercentCoder p = RFC3986.FRAGMENT.orNew("%");
    /**
     * 协议，例如http
     */
    private String scheme;
    /**
     * 主机，例如127.0.0.1
     */
    private String host;
    /**
     * 端口，默认-1
     */
    private int port = -1;
    /**
     * 路径，例如/aa/bb/cc
     */
    private UrlPath path;
    /**
     * 查询语句，例如a=1&amp;b=2
     */
    private UrlQuery query;
    /**
     * 标识符，例如#后边的部分
     */
    private String fragment;

    /**
     * 编码，用于URLEncode和URLDecode
     */
    private Charsets charsets;
    /**
     * 是否需要编码`%`<br>
     * 区别对待，如果是，则生成URL时需要重新全部编码，否则跳过所有`%`
     */
    private boolean needEncodePercent;

    /**
     * 构造
     */
    public UrlBuilder() {
        this.charsets = Charsets.UTF_8;
    }

    /**
     * 构造
     *
     * @param scheme   协议，默认http
     * @param host     主机，例如127.0.0.1
     * @param port     端口，-1表示默认端口
     * @param path     路径，例如/aa/bb/cc
     * @param query    查询，例如a=1&amp;b=2
     * @param fragment 标识符例如#后边的部分
     * @param charsets 编码，用于URLEncode和URLDecode，{@code null}表示不编码
     */
    public UrlBuilder(
            String scheme,
            String host,
            int port,
            UrlPath path,
            UrlQuery query,
            String fragment,
            Charsets charsets) {
        this.charsets = charsets;
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.setFragment(fragment);
        // 编码非空情况下做解码
        this.needEncodePercent = null != charsets;
    }

    /**
     * 使用URI构建UrlBuilder
     *
     * @param uri      URI
     * @param charsets 编码，用于URLEncode和URLDecode
     * @return UrlBuilder
     */
    public static UrlBuilder of(URI uri, Charsets charsets) {
        return of(
                uri.getScheme(),
                uri.getHost(),
                uri.getPort(),
                uri.getPath(),
                uri.getRawQuery(),
                uri.getFragment(),
                charsets);
    }

    /**
     * 使用URL字符串构建UrlBuilder，当传入的URL没有协议时，按照http协议对待<br>
     * 此方法不对URL编码
     *
     * @param httpUrl URL字符串
     * @return UrlBuilder
     */
    public static UrlBuilder ofHttpWithoutEncode(String httpUrl) {
        return ofHttp(httpUrl, null);
    }

    /**
     * 使用URL字符串构建UrlBuilder，当传入的URL没有协议时，按照http协议对待，编码默认使用UTF-8
     *
     * @param httpUrl URL字符串
     * @return UrlBuilder
     */
    public static UrlBuilder ofHttp(String httpUrl) {
        return ofHttp(httpUrl, Charsets.UTF_8);
    }

    /**
     * 使用URL字符串构建UrlBuilder，当传入的URL没有协议时，按照http协议对待。
     *
     * @param httpUrl  URL字符串
     * @param charsets 编码，用于URLEncode和URLDecode
     * @return UrlBuilder
     */
    public static UrlBuilder ofHttp(String httpUrl, Charsets charsets) {
        Assert.notBlank(httpUrl, "Http url must be not blank!");

        final int sepIndex = httpUrl.indexOf("://");
        if (sepIndex < 0) {
            httpUrl = "http://" + httpUrl.trim();
        }
        return of(httpUrl, charsets);
    }

    /**
     * 使用URL字符串构建UrlBuilder，默认使用UTF-8编码
     *
     * @param url URL字符串
     * @return UrlBuilder
     */
    public static UrlBuilder of(String url) {
        return of(url, Charsets.UTF_8);
    }

    /**
     * 使用URL字符串构建UrlBuilder
     *
     * @param url      URL字符串
     * @param charsets 编码，用于URLEncode和URLDecode
     * @return UrlBuilder
     */
    public static UrlBuilder of(String url, Charsets charsets) {
        Assert.notBlank(url, "Url must be not blank!");
        return of(ToolURL.url(ToolString.trim(url)), charsets);
    }

    /**
     * 使用URL构建UrlBuilder
     *
     * @param url      URL
     * @param charsets 编码，用于URLEncode和URLDecode
     * @return UrlBuilder
     */
    public static UrlBuilder of(URL url, Charsets charsets) {
        return of(
                url.getProtocol(),
                url.getHost(),
                url.getPort(),
                url.getPath(),
                url.getQuery(),
                url.getRef(),
                charsets);
    }

    /**
     * 构建UrlBuilder
     *
     * @param scheme   协议，默认http
     * @param host     主机，例如127.0.0.1
     * @param port     端口，-1表示默认端口
     * @param path     路径，例如/aa/bb/cc
     * @param query    查询，例如a=1&amp;b=2
     * @param fragment 标识符例如#后边的部分
     * @param charsets 编码，用于URLEncode和URLDecode
     * @return UrlBuilder
     */
    public static UrlBuilder of(
            String scheme,
            String host,
            int port,
            String path,
            String query,
            String fragment,
            Charsets charsets) {
        return of(
                scheme,
                host,
                port,
                UrlPath.of(path, charsets),
                UrlQuery.of(query, charsets, false),
                fragment,
                charsets);
    }

    /**
     * 构建UrlBuilder
     *
     * @param scheme   协议，默认http
     * @param host     主机，例如127.0.0.1
     * @param port     端口，-1表示默认端口
     * @param path     路径，例如/aa/bb/cc
     * @param query    查询，例如a=1&amp;b=2
     * @param fragment 标识符例如#后边的部分
     * @param charsets 编码，用于URLEncode和URLDecode
     * @return UrlBuilder
     */
    public static UrlBuilder of(
            String scheme,
            String host,
            int port,
            UrlPath path,
            UrlQuery query,
            String fragment,
            Charsets charsets) {
        return new UrlBuilder(scheme, host, port, path, query, fragment, charsets);
    }

    /**
     * 创建空的UrlBuilder
     *
     * @return UrlBuilder
     */
    public static UrlBuilder create() {
        return new UrlBuilder();
    }

    /**
     * 获取协议，例如http
     *
     * @return 协议，例如http
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * 设置协议，例如http
     *
     * @param scheme 协议，例如http
     * @return this
     */
    public UrlBuilder setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * 获取协议，例如http，如果用户未定义协议，使用默认的http协议
     *
     * @return 协议，例如http
     */
    public String getSchemeWithDefault() {
        return Safe.value(this.scheme, DEFAULT_SCHEME);
    }

    /**
     * 获取 主机，例如127.0.0.1
     *
     * @return 主机，例如127.0.0.1
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置主机，例如127.0.0.1
     *
     * @param host 主机，例如127.0.0.1
     * @return this
     */
    public UrlBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * 获取端口，默认-1
     *
     * @return 端口，默认-1
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置端口，默认-1
     *
     * @param port 端口，默认-1
     * @return this
     */
    public UrlBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * 获得authority部分
     *
     * @return authority部分
     */
    public String getAuthority() {
        return (port < 0) ? host : host + ":" + port;
    }

    /**
     * 获取路径，例如/aa/bb/cc
     *
     * @return 路径，例如/aa/bb/cc
     */
    public UrlPath getPath() {
        return path;
    }

    /**
     * 设置路径，例如/aa/bb/cc，将覆盖之前所有的path相关设置
     *
     * @param path 路径，例如/aa/bb/cc
     * @return this
     */
    public UrlBuilder setPath(UrlPath path) {
        this.path = path;
        return this;
    }

    /**
     * 获得路径，例如/aa/bb/cc
     *
     * @return 路径，例如/aa/bb/cc
     */
    public String getPathStr() {
        return null == this.path ? PoolOfString.SLASH : this.path.build(charsets, this.needEncodePercent);
    }

    /**
     * 是否path的末尾加 /
     *
     * @param withEngTag 是否path的末尾加 /
     * @return this
     */
    public UrlBuilder setWithEndTag(boolean withEngTag) {
        if (null == this.path) {
            this.path = new UrlPath();
        }

        this.path.setWithEndTag(withEngTag);
        return this;
    }

    /**
     * 增加路径，在现有路径基础上追加路径
     *
     * @param path 路径，例如aaa/bbb/ccc
     * @return this
     */
    public UrlBuilder addPath(CharSequence path) {
        UrlPath.of(path, this.charsets).getSegments().forEach(this::addPathSegment);
        return this;
    }

    /**
     * 增加路径节点，路径节点中的"/"会被转义为"%2F"
     *
     * @param segment 路径节点
     * @return this
     */
    public UrlBuilder addPathSegment(CharSequence segment) {
        if (Whether.empty(segment)) {
            return this;
        }
        if (null == this.path) {
            this.path = new UrlPath();
        }
        this.path.add(segment);
        return this;
    }

    /**
     * 追加path节点
     *
     * @param path path节点
     * @return this
     * @deprecated 方法重复，请使用{@link #addPath(CharSequence)}
     */
    @Deprecated
    public UrlBuilder appendPath(CharSequence path) {
        return addPath(path);
    }

    /**
     * 获取查询语句，例如a=1&amp;b=2<br>
     * 可能为{@code null}
     *
     * @return 查询语句，例如a=1&amp;b=2，可能为{@code null}
     */
    public UrlQuery getQuery() {
        return query;
    }

    /**
     * 设置查询语句，例如a=1&amp;b=2，将覆盖之前所有的query相关设置
     *
     * @param query 查询语句，例如a=1&amp;b=2
     * @return this
     */
    public UrlBuilder setQuery(UrlQuery query) {
        this.query = query;
        return this;
    }

    /**
     * 获取查询语句，例如a=1&amp;b=2
     *
     * @return 查询语句，例如a=1&amp;b=2
     */
    public String getQueryStr() {
        return null == this.query ? null : this.query.build(this.charsets, this.needEncodePercent);
    }

    /**
     * 添加查询项，支持重复键
     *
     * @param key   键
     * @param value 值
     * @return this
     */
    public UrlBuilder addQuery(String key, Object value) {
        if (Whether.empty(key)) {
            return this;
        }

        if (this.query == null) {
            this.query = new UrlQuery();
        }
        this.query.add(key, value);
        return this;
    }

    /**
     * 获取标识符，#后边的部分
     *
     * @return 标识符，例如#后边的部分
     */
    public String getFragment() {
        return fragment;
    }

    /**
     * 设置标识符，例如#后边的部分
     *
     * @param fragment 标识符，例如#后边的部分
     * @return this
     */
    public UrlBuilder setFragment(String fragment) {
        if (Whether.empty(fragment)) {
            this.fragment = null;
        }
        this.fragment = ToolString.removePrefix(fragment, "#");
        return this;
    }

    /**
     * 获取标识符，#后边的部分
     *
     * @return 标识符，例如#后边的部分
     */
    public String getFragmentEncoded() {
        return p.setCharsets(charsets).encodeString(this.fragment);
    }

    /**
     * 获取编码，用于URLEncode和URLDecode
     *
     * @return 编码
     */
    public Charsets getCharsets() {
        return charsets;
    }

    /**
     * 设置编码，用于URLEncode和URLDecode
     *
     * @param charsets 编码
     * @return this
     */
    public UrlBuilder setCharsets(Charsets charsets) {
        this.charsets = charsets;
        return this;
    }

    /**
     * 创建URL字符串
     *
     * @return URL字符串
     */
    @Override
    public String build() {
        return toURL().toString();
    }

    /**
     * 转换为{@link URL} 对象
     *
     * @return {@link URL}
     */
    public URL toURL() {
        return toURL(null);
    }

    /**
     * 转换为{@link URL} 对象
     *
     * @param handler {@link URLStreamHandler}，null表示默认
     * @return {@link URL}
     */
    public URL toURL(URLStreamHandler handler) {
        final StringBuilder fileBuilder = new StringBuilder();

        // path
        fileBuilder.append(getPathStr());

        // query
        final String query = getQueryStr();
        if (Whether.noBlank(query)) {
            fileBuilder.append('?').append(query);
        }

        // fragment
        if (Whether.noBlank(this.fragment)) {
            fileBuilder.append('#').append(getFragmentEncoded());
        }

        try {
            return new URL(getSchemeWithDefault(), host, port, fileBuilder.toString(), handler);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * 转换为URI
     *
     * @return URI
     */
    public URI toURI() {
        try {
            return new URI(
                    getSchemeWithDefault(),
                    getAuthority(),
                    getPathStr(),
                    getQueryStr(),
                    getFragmentEncoded());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return build();
    }
}
