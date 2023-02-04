package pxf.tl.api;

import pxf.tl.help.Whether;

import java.io.Serial;
import java.util.Arrays;

/**
 * 可变数组类型（元组）
 *
 * @author potatoxf
 */
public class MutableTuple<T> extends Tuple<T> {
    @Serial
    private static final long serialVersionUID = 1L;

    @SafeVarargs
    public MutableTuple(T... values) {
        super(values);
    }

    public MutableTuple(Tuple<T> tuple) {
        super(tuple);
    }

    public MutableTuple(Tuple<T> tuple, T[] values) {
        super(tuple, values);
    }

    /**
     * 添加元素
     *
     * @param tuple 数组对象
     */
    public final void extend(Tuple<T> tuple) {
        if (tuple != null) {
            extend(tuple.values);
        }
    }

    /**
     * 添加元素
     *
     * @param array 数组
     */
    @SafeVarargs
    public final void extend(T... array) {
        if (Whether.noEmpty(array)) {
            int newLen = values.length + array.length;
            T[] newArr = Arrays.copyOf(values, newLen);
            System.arraycopy(array, 0, newArr, values.length, array.length);
            values = newArr;
            hashCode = 0;
        }
    }
}
