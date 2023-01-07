package pxf.tl.net.url;


import pxf.tl.api.Charsets;
import pxf.tl.api.PoolOfCharacter;
import pxf.tl.api.PoolOfString;
import pxf.tl.help.Assert;
import pxf.tl.help.Whether;
import pxf.tl.net.RFC3986;
import pxf.tl.util.ToolString;
import pxf.tlx.codec.impl.PercentCoder;

import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

/**
 * URL中Path部分的封装
 *
 * @author potatoxf
 */
public class UrlPath {

    private List<String> segments;
    private boolean withEngTag;

    /**
     * 构建UrlPath
     *
     * @param pathStr  初始化的路径字符串
     * @param charsets decode用的编码，null表示不做decode
     * @return UrlPath
     */
    public static UrlPath of(CharSequence pathStr, Charsets charsets) {
        final UrlPath urlPath = new UrlPath();
        urlPath.parse(pathStr, charsets);
        return urlPath;
    }

    /**
     * 修正路径，包括去掉前后的/，去掉空白符
     *
     * @param path 节点或路径path
     * @return 修正后的路径
     */
    private static String fixPath(CharSequence path) {
        Assert.notNull(path, "Path segment must be not null!");
        if ("/".contentEquals(path)) {
            return ToolString.EMPTY;
        }

        String segmentStr = ToolString.trim(path);
        segmentStr = ToolString.removePrefix(segmentStr, PoolOfString.SLASH);
        segmentStr = ToolString.removeSuffix(segmentStr, PoolOfString.SLASH);
        segmentStr = ToolString.trim(segmentStr);
        return segmentStr;
    }

    /**
     * 是否path的末尾加 /
     *
     * @param withEngTag 是否path的末尾加 /
     * @return this
     */
    public UrlPath setWithEndTag(boolean withEngTag) {
        this.withEngTag = withEngTag;
        return this;
    }

    /**
     * 获取path的节点列表
     *
     * @return 节点列表
     */
    public List<String> getSegments() {
        return this.segments;
    }

    /**
     * 获得指定节点
     *
     * @param index 节点位置
     * @return 节点，无节点或者越界返回null
     */
    public String getSegment(int index) {
        if (null == this.segments || index >= this.segments.size()) {
            return null;
        }
        return this.segments.get(index);
    }

    /**
     * 添加到path最后面
     *
     * @param segment Path节点
     * @return this
     */
    public UrlPath add(CharSequence segment) {
        addInternal(fixPath(segment), false);
        return this;
    }

    /**
     * 添加到path最前面
     *
     * @param segment Path节点
     * @return this
     */
    public UrlPath addBefore(CharSequence segment) {
        addInternal(fixPath(segment), true);
        return this;
    }

    /**
     * 解析path
     *
     * @param path     路径，类似于aaa/bb/ccc或/aaa/bbb/ccc
     * @param charsets decode编码，null表示不解码
     * @return this
     */
    public UrlPath parse(CharSequence path, Charsets charsets) {
        if (Whether.noEmpty(path)) {
            // 原URL中以/结尾，则这个规则需保留，issue#I1G44J@Gitee
            if (ToolString.endWith(path, PoolOfCharacter.SLASH)) {
                this.withEngTag = true;
            }

            path = fixPath(path);
            if (Whether.noEmpty(path)) {
                final List<String> split = ToolString.split(path, '/');
                for (String seg : split) {
                    addInternal(URLDecoder.decode(seg, charsets.get()), false);
                }
            }
        }

        return this;
    }

    /**
     * 构建path，前面带'/'<br>
     *
     * <pre>
     *     path = path-abempty / path-absolute / path-noscheme / path-rootless / path-empty
     * </pre>
     *
     * @param charsets encode编码，null表示不做encode
     * @return 如果没有任何内容，则返回空字符串""
     */
    public String build(Charsets charsets) {
        return build(charsets, true);
    }

    /**
     * 构建path，前面带'/'<br>
     *
     * <pre>
     *     path = path-abempty / path-absolute / path-noscheme / path-rootless / path-empty
     * </pre>
     *
     * @param charsets      encode编码，null表示不做encode
     * @param encodePercent 是否编码`%`
     * @return 如果没有任何内容，则返回空字符串""
     */
    public String build(Charsets charsets, boolean encodePercent) {
        if (Whether.empty(this.segments)) {
            // 没有节点的path取决于是否末尾追加/，如果不追加返回空串，否则返回/
            return withEngTag ? PoolOfString.SLASH : ToolString.EMPTY;
        }

        PercentCoder percentCoder = RFC3986.SEGMENT_NZ_NC.orNew("%").setCharsets(charsets);
        final char[] safeChars = encodePercent ? null : new char[]{'%'};
        final StringBuilder builder = new StringBuilder();
        for (final String segment : segments) {
            if (builder.length() == 0) {
                // 根据https://www.ietf.org/rfc/rfc3986.html#section-3.3定义
                // path的第一部分不允许有":"，其余部分允许
                // 在此处的Path部分特指host之后的部分，即不包含第一部分
                builder
                        .append(PoolOfCharacter.SLASH)
                        .append(percentCoder.encodeString(segment));
            } else {
                builder.append(PoolOfCharacter.SLASH).append(percentCoder.encodeString(segment));
            }
        }

        if (withEngTag) {
            if (Whether.empty(builder)) {
                // 空白追加是保证以/开头
                builder.append(PoolOfCharacter.SLASH);
            } else if (!ToolString.endWith(builder, PoolOfCharacter.SLASH)) {
                // 尾部没有/则追加，否则不追加
                builder.append(PoolOfCharacter.SLASH);
            }
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return build(null);
    }

    /**
     * 增加节点
     *
     * @param segment 节点
     * @param before  是否在前面添加
     */
    private void addInternal(CharSequence segment, boolean before) {
        if (this.segments == null) {
            this.segments = new LinkedList<>();
        }

        final String seg = ToolString.str(segment);
        if (before) {
            this.segments.add(0, seg);
        } else {
            this.segments.add(seg);
        }
    }
}
