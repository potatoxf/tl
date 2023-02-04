package pxf.tl.collection.map;

import java.util.HashMap;

/**
 * 分组统计计算
 *
 * <p>统计每个键出现的次数
 *
 * @author potatoxf
 */
public class GroupStatisticalMap<K> extends HashMap<K, Integer> {

    private final int defaultInitValue;

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative or the load factor is
     *                                  nonpositive
     */
    public GroupStatisticalMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 0);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative or the load factor is
     *                                  nonpositive
     */
    public GroupStatisticalMap(int initialCapacity, float loadFactor, int defaultInitValue) {
        super(initialCapacity, loadFactor);
        this.defaultInitValue = defaultInitValue;
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity (16) and the default
     * load factor (0.75).
     */
    public GroupStatisticalMap(int defaultInitValue) {
        this.defaultInitValue = defaultInitValue;
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity (16) and the default
     * load factor (0.75).
     */
    public GroupStatisticalMap() {
        this(0);
    }

    /**
     * 在原来基础上加值，如果没有则创建值再加值
     *
     * @param key   键
     * @param value 值
     * @return 返回新值
     */
    public int add(K key, int value) {
        Integer oldValue = get(key);
        int newValue;
        if (oldValue == null) {
            newValue = defaultInitValue + value;
        } else {
            newValue = oldValue + value;
        }
        put(key, newValue);
        return newValue;
    }

    /**
     * 在原来基础上自加值，如果没有则创建值自加值
     *
     * @param key 键
     * @return 返回新值
     */
    public int increase(K key) {
        return add(key, 1);
    }

    /**
     * 在原来基础上减值，如果没有则创建值再减值
     *
     * @param key   键
     * @param value 值
     * @return 返回新值
     */
    public int sub(K key, int value) {
        Integer oldValue = get(key);
        int newValue;
        if (oldValue == null) {
            newValue = defaultInitValue - value;
        } else {
            newValue = oldValue - value;
        }
        put(key, newValue);
        return newValue;
    }

    /**
     * 在原来基础上自减值，如果没有则创建值自减值
     *
     * @param key 键
     * @return 返回新值
     */
    public int decrease(K key) {
        return sub(key, 1);
    }

    /**
     * 重置键的值
     *
     * @param key 键
     */
    public void reset(K key) {
        put(key, defaultInitValue);
    }
}
