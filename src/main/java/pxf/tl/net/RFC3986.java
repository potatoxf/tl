package pxf.tl.net;


import pxf.tl.api.PoolOfString;
import pxf.tlx.codec.impl.PercentCoder;

import java.util.BitSet;

/**
 * <a href="https://www.ietf.org/rfc/rfc3986.html">RFC3986</a> 编码实现<br>
 * 定义见：<a
 * href="https://www.ietf.org/rfc/rfc3986.html#appendix-A">https://www.ietf.org/rfc/rfc3986.html#appendix-A</a>
 *
 * @author potatoxf
 */
public class RFC3986 {

    /**
     * gen-delims = ":" / "/" / "?" / "#" / "[" / "]" / "@"
     */
    public static final PercentCoder GEN_DELIMS = PercentCoder.of(":/?#[]@");

    /**
     * sub-delims = "!" / "$" / "{@code &}" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     */
    public static final PercentCoder SUB_DELIMS = PercentCoder.of("!$&'()*+,;=");

    /**
     * reserved = gen-delims / sub-delims<br>
     * see：<a
     * href="https://www.ietf.org/rfc/rfc3986.html#section-2.2">https://www.ietf.org/rfc/rfc3986.html#section-2.2</a>
     */
    public static final PercentCoder RESERVED = GEN_DELIMS.orNew(SUB_DELIMS);

    /**
     * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"<br>
     * see: <a
     * href="https://www.ietf.org/rfc/rfc3986.html#section-2.3">https://www.ietf.org/rfc/rfc3986.html#section-2.3</a>
     */
    public static final PercentCoder UNRESERVED = PercentCoder.of(unreservedChars());

    /**
     * pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
     */
    public static final PercentCoder PCHAR = UNRESERVED.orNew(SUB_DELIMS, ":@");

    /**
     * segment = pchar<br>
     * see: <a
     * href="https://www.ietf.org/rfc/rfc3986.html#section-3.3">https://www.ietf.org/rfc/rfc3986.html#section-3.3</a>
     */
    public static final PercentCoder SEGMENT = PCHAR;
    /**
     * segment-nz-nc = SEGMENT ; non-zero-length segment without any colon ":"
     */
    public static final PercentCoder SEGMENT_NZ_NC = SEGMENT.orNew("", ":");

    /**
     * path = segment / "/"
     */
    public static final PercentCoder PATH = SEGMENT.orNew("/");

    /**
     * query = pchar / "/" / "?"
     */
    public static final PercentCoder QUERY = PCHAR.orNew("/?");

    /**
     * fragment = pchar / "/" / "?"
     */
    public static final PercentCoder FRAGMENT = QUERY;

    /**
     * query中的value<br>
     * value不能包含"{@code &}"，可以包含 "="
     */
    public static final PercentCoder QUERY_PARAM_VALUE = QUERY.orNew("", "&");

    /**
     * query中的key<br>
     * key不能包含"{@code &}" 和 "="
     */
    public static final PercentCoder QUERY_PARAM_NAME = QUERY_PARAM_VALUE.orNew("", "=");

    /**
     * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
     *
     * @return unreserved字符
     */
    private static BitSet unreservedChars() {

        BitSet sb = PoolOfString.getLetterNumberBitSet();
        // "-" / "." / "_" / "~"
        sb.set('-');
        sb.set('.');
        sb.set('-');
        sb.set('~');

        return sb;
    }
}
