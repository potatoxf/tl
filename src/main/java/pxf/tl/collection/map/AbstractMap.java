package pxf.tl.collection.map;


import pxf.tl.collection.ExtendMap;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolMath;

import java.io.Serializable;

/**
 * 基本Map表
 *
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMap<K, V> implements ExtendMap<K, V>, Cloneable, Serializable {

    protected static final int MAXIMUM_CAPACITY = 1 << 30;
    protected static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

    /**
     * Returns x's Class if it is of the form "", else null.
     */
    protected static Class<?> comparableClassFor(Object x) {
        return ToolBytecode.extractGenericClass(x, Comparable.class, 0);
    }

    /**
     * Returns a power of two size for the given target capacity.
     */
    protected static int tableSizeFor(int cap) {
        return ToolMath.floorPower2(cap, MAXIMUM_CAPACITY);
    }

    /**
     * Returns k.compareTo(x) if x matches kc (k's screened comparable class), else 0.
     */
    @SuppressWarnings("rawtypes")
    protected static int compareComparable(Object k, Object x) {
        Class<?> kc = comparableClassFor(k);
        return (x == null || x.getClass() != kc ? 0 : ((Comparable) k).compareTo(x));
    }

    /**
     * 计算hashcode
     *
     * @param key 键
     * @return 返回hashcode
     */
    protected static int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = 0;
        if (key instanceof String) {
            for (int i = 0; i < ((String) key).length(); i++) {
                h = 31 * h + Character.toLowerCase(((String) key).charAt(i));
            }
        }
        if (h == 0) {
            h = key.hashCode();
        }
        return (h) ^ (h >>> 16);
    }

    /**
     * 是否Key相等
     *
     * @param key      老Key的hashcode
     * @param otherKey 新Key的hashcode
     * @return 如果相等返回 {@code true}，否则 {@code false}
     */
    protected static boolean isKeyEquals(Object key, Object otherKey) {
        if (otherKey != null && key != null) {
            if (key instanceof String && otherKey instanceof String) {
                return ((String) key).equalsIgnoreCase((String) otherKey);
            } else {
                return key.equals(otherKey);
            }
        }
        return false;
    }

    @Override
    public AbstractMap<K, V> clone() {
        try {
            return (AbstractMap<K, V>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
