package pxf.tl.text.escape;


import pxf.tl.text.replacer.LookupReplacer;
import pxf.tl.text.replacer.ReplacerChain;

/**
 * XML的UNESCAPE
 *
 * @author potatoxf
 */
public class XmlUnescape extends ReplacerChain {
    protected static final String[][] BASIC_UNESCAPE =
            InternalEscapeUtil.invert(XmlEscape.BASIC_ESCAPE);
    // issue#1118
    protected static final String[][] OTHER_UNESCAPE = new String[][]{new String[]{"&apos;", "'"}};
    private static final long serialVersionUID = 1L;

    /**
     * 构造
     */
    public XmlUnescape() {
        addChain(new LookupReplacer(BASIC_UNESCAPE));
        addChain(new NumericEntityUnescaper());
        addChain(new LookupReplacer(OTHER_UNESCAPE));
    }
}
