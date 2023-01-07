package pxf.tl.api;

import java.util.Collections;

/**
 * @author potatoxf
 */
public interface PoolOfCommon {
    /**
     *
     */
    int INITIAL_HASH = 7;
    /**
     *
     */
    int MULTIPLIER = 31;
    /**
     * false
     */
    boolean FALSE = false;
    /**
     * true
     */
    boolean TRUE = true;
    /**
     * 无效数字，适合所有数字返回值判断
     */
    int EOF = -1;
    /**
     * 零
     */
    byte ZERO_BYTE = 0;
    /**
     * 零
     */
    double ZERO_DOUBLE = 0;
    /**
     * 零
     */
    float ZERO_FLOAT = 0;
    /**
     * 零
     */
    int ZERO_INT = 0;
    /**
     * 零
     */
    long ZERO_LONG = 0;
    /**
     * 零
     */
    short ZERO_SHORT = 0;
    /**
     * 8位
     */
    int BYTE = 8;
    /**
     * 16位
     */
    int WORD = 16;
    /**
     * 32位
     */
    int DWORD = 32;
    /**
     * 64位
     */
    int QWORD = 64;
    Iterable<Object> ITERABLE = Collections::emptyIterator;

    static <T> Iterable<T> iterable() {
        return (Iterable<T>) ITERABLE;
    }
}
