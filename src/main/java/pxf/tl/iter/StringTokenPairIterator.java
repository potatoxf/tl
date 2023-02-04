package pxf.tl.iter;


import pxf.tl.api.Pair;

import java.util.Iterator;

/**
 * @author potatoxf
 */
public class StringTokenPairIterator extends AbstractIter<Pair<String, String>> {
    private final StringTokenIterator stringTokenIterator;

    /**
     * @param stringIterator
     * @param splitRegexp
     */
    public StringTokenPairIterator(Iterator<String> stringIterator, String splitRegexp) {
        stringTokenIterator = new InnerStringTokenIterator(stringIterator, splitRegexp);
    }

    /**
     * 计算新的节点，通过实现此方法，当调用{@link #hasNext()}时将此方法产生的节点缓存，直到调用{@link #next()}取出<br>
     * 当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    @Override
    protected Pair<String, String> doNext() {
        while (stringTokenIterator.hasNext()) {
            String[] result = stringTokenIterator.next();
            if (result == null) {
                continue;
            }
            String key = resolveKey(result);
            String value = resolveValue(result);
            return new Pair<>(key, value);
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
     * @param result
     * @return
     */
    protected String resolveKey(String[] result) {
        return result[0];
    }

    /**
     * @param result
     * @return
     */
    protected String resolveValue(String[] result) {
        return result[1];
    }

    /**
     *
     */
    private class InnerStringTokenIterator extends StringTokenIterator {

        /**
         * @param stringIterator
         * @param splitRegexp
         */
        public InnerStringTokenIterator(Iterator<String> stringIterator, String splitRegexp) {
            super(stringIterator, splitRegexp, 2);
        }

        /**
         * @param string
         * @return
         */
        @Override
        protected String preprocessedString(String string) {
            return StringTokenPairIterator.this.preprocessedString(string);
        }
    }
}
