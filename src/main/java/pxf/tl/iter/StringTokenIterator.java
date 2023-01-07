package pxf.tl.iter;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 字符串Token迭代器
 *
 * @author potatoxf
 */
public class StringTokenIterator extends AbstractIter<String[]> {

    private final Iterator<String> stringIterator;
    private final String splitRegexp;
    private final int fixedLength;

    /**
     * @param stringIterator
     * @param splitRegexp
     * @param fixedLength
     */
    public StringTokenIterator(Iterator<String> stringIterator, String splitRegexp, int fixedLength) {
        this.stringIterator = stringIterator;
        this.splitRegexp = splitRegexp;
        this.fixedLength = fixedLength;
    }

    /**
     * 计算新的节点，通过实现此方法，当调用{@link #hasNext()}时将此方法产生的节点缓存，直到调用{@link #next()}取出<br>
     * 当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    @Override
    protected String[] doNext() {
        if (stringIterator.hasNext()) {
            String string = stringIterator.next();
            string = preprocessedString(string);
            if (string == null) {
                String[] empty = new String[fixedLength];
                Arrays.fill(empty, "");
                return empty;
            }
            String[] result = string.split(splitRegexp, fixedLength);
            if (result.length < fixedLength) {
                String[] copy = new String[fixedLength];
                System.arraycopy(result, 0, copy, 0, result.length);
                Arrays.fill(copy, result.length, fixedLength, "");
                return copy;
            }
            return result;
        }
        return null;
    }


    /**
     * @param string
     * @return
     */
    protected String preprocessedString(String string) {
        return string;
    }

    /**
     * @return
     */
    public String getSplitRegexp() {
        return splitRegexp;
    }

    /**
     * @return
     */
    public int getFixedLength() {
        return fixedLength;
    }
}
