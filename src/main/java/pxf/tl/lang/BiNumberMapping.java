package pxf.tl.lang;


import pxf.tl.help.Safe;

import java.util.HashMap;
import java.util.Map;

/**
 * 双数字映射成单数字
 *
 * <p>用于类似双类型装换成单类型
 *
 * @author potatoxf
 */
public final class BiNumberMapping {
    private static final String DEFAULT_ARRAY_SPLIT_CHAR = ",";
    private static final String DEFAULT_GROUP_SPLIT_CHAR = ";";
    private static final String DEFAULT_ONE_PAIR_SPLIT_CHAR = ":";
    private static final String DEFAULT_TWO_PAIR_SPLIT_CHAR = "=";
    private final Map<Integer, Map<Integer, Integer>> container =
            new HashMap<Integer, Map<Integer, Integer>>();
    private final String arraySplitChar;
    private final String groupSplitChar;
    private final String onePairSplitChar;
    private final String twoPairSplitChar;

    public BiNumberMapping() {
        this(
                DEFAULT_ARRAY_SPLIT_CHAR,
                DEFAULT_GROUP_SPLIT_CHAR,
                DEFAULT_ONE_PAIR_SPLIT_CHAR,
                DEFAULT_TWO_PAIR_SPLIT_CHAR);
    }

    public BiNumberMapping(
            String arraySplitChar,
            String groupSplitChar,
            String onePairSplitChar,
            String twoPairSplitChar) {
        this.arraySplitChar = arraySplitChar;
        this.groupSplitChar = groupSplitChar;
        this.onePairSplitChar = onePairSplitChar;
        this.twoPairSplitChar = twoPairSplitChar;
    }

    /**
     * 添加双数字映射值
     *
     * @param expression 表达式
     */
    public void add(String expression) {
        if (expression != null) {
            expression = expression.trim();
            for (String str : expression.split(groupSplitChar)) {
                if (str == null) {
                    continue;
                }
                str = str.trim();

                String[] arr = str.split(onePairSplitChar);

                if (arr.length != 2) {
                    continue;
                }
                Integer[] as = Safe.toIntegerArray(arr[0].split(arraySplitChar));
                String[] pair = arr[1].split(twoPairSplitChar);
                if (pair.length != 2) {
                    continue;
                }

                Integer[] bs = Safe.toIntegerArray(pair[0].split(arraySplitChar));
                Integer[] cs = Safe.toIntegerArray(pair[1].split(arraySplitChar));
                if (bs.length == 0 || cs.length == 0) {
                    continue;
                }
                for (Integer a : as) {
                    Map<Integer, Integer> map =
                            container.computeIfAbsent(a, k -> new HashMap<>());
                    for (int b : bs) {
                        for (int c : cs) {
                            map.put(b, c);
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加双数字映射值
     *
     * @param key1  键1
     * @param key2  键2
     * @param value 值
     */
    public void add(int key1, int key2, int value) {
        Map<Integer, Integer> map =
                container.computeIfAbsent(key1, k -> new HashMap<Integer, Integer>());
        map.put(key2, value);
    }

    /**
     * 是否包含键1
     *
     * @param key1 键1
     * @return 如果包含返回true，否则返回false
     */
    public boolean containsKey(int key1) {
        return container.containsKey(key1);
    }

    /**
     * 是否包含键1，键2
     *
     * @param key1 键1
     * @param key2 键2
     * @return 如果包含返回true，否则返回false
     */
    public boolean containsKey(int key1, int key2) {
        return container.containsKey(key1) && container.get(key1).containsKey(key2);
    }

    /**
     * 获取值
     *
     * @param key1 键1
     * @return Map<Integer, Integer>
     */
    public Map<Integer, Integer> getValue(int key1) {
        Map<Integer, Integer> result = container.get(key1);
        if (result == null) {
            throw new IllegalArgumentException("Not in the key [" + key1 + "]");
        }
        return result;
    }

    /**
     * 获取值
     *
     * @param key1 键1
     * @param key2 键2
     * @return int
     */
    public int getValue(int key1, int key2) {
        Map<Integer, Integer> map = getValue(key1);
        Number result = map.get(key2);
        if (result == null) {
            throw new IllegalArgumentException("Not in key【" + key1 + "-" + key2 + "]");
        }
        return result.intValue();
    }

    /**
     * 移除值
     *
     * @param key1 键1
     */
    public void remove(int key1) {
        container.remove(key1);
    }

    /**
     * 移除值
     *
     * @param key1 键1
     * @param key2 键2
     */
    public void remove(int key1, int key2) {
        Map<Integer, Integer> map = container.get(key1);
        if (map != null) {
            map.remove(key2);
        }
    }

    /**
     * 清空
     */
    public void clear() {
        container.clear();
    }

    /**
     * Returns a string representation of the object. In general, the {@code toString} method returns
     * a string that "textually represents" this object. The result should be a concise but
     * informative representation that is easy for a person to read. It is recommended that all
     * subclasses override this method.
     *
     * <p>The {@code toString} method for class {@code Object} returns a string consisting of the name
     * of the class of which the object is an instance, the at-sign character `{@code @}', and the
     * unsigned hexadecimal representation of the hash code of the object. In other words, this method
     * returns a string equal to the value of:
     *
     * <blockquote>
     *
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre>
     *
     * </blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return container.toString();
    }
}
