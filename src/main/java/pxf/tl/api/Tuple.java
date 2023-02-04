package pxf.tl.api;


import pxf.tl.help.New;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.iter.AnyIter;
import pxf.tl.util.ToolArray;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 不可变数组类型（元组）
 *
 * @author potatoxf
 */
public class Tuple<T> implements Iterable<T>, Serializable, Sized {

    @Serial
    private static final long serialVersionUID = 1L;
    protected volatile T[] values;
    protected int hashCode = 0;

    @SafeVarargs
    public Tuple(T... values) {
        this.values = values;
    }

    public Tuple(Tuple<T> tuple) {
        this.values = tuple.values;
        this.hashCode = tuple.hashCode;
    }

    public Tuple(Tuple<T> tuple, T[] values) {
        if (Whether.empty(values)) {
            this.values = tuple.values;
        }
        int newLen = tuple.values.length + values.length;
        T[] newArr = Arrays.copyOf(tuple.values, newLen);
        System.arraycopy(values, 0, newArr, tuple.values.length, values.length);
        this.values = newArr;
    }

    /**
     * 数组长度
     *
     * @return 长度
     */
    public int size() {
        return values.length;
    }

    /**
     * 获取元素
     *
     * @param index 元素索引，任何数值都可以转化成合法索引 {@link Safe#idx(int, int)}
     * @return 返回元素
     */
    public T get(int index) {
        return values[Safe.idx(index, values.length)];
    }

    /**
     * 判断元组中是否包含某元素
     *
     * @param value 需要判定的元素
     * @return 是否包含
     */
    public boolean contains(T value) {
        return ToolArray.contains(this.values, value);
    }

    /**
     * 截取元组指定部分
     *
     * @param start 起始位置（包括）
     * @param end   终止位置（不包括）
     * @return 截取得到的元组
     */
    public Tuple<T> sub(int start, int end) {
        return new Tuple<>(Arrays.copyOfRange(values, Safe.idx(start, values.length), Safe.idx(end, values.length)));
    }

    /**
     * 将元组转换成列表
     *
     * @return 转换得到的列表
     */
    public List<T> toList() {
        return New.list(false, values);
    }

    /**
     * 将元组转成流
     *
     * @return 流
     */
    public Stream<T> stream() {
        return Arrays.stream(values);
    }

    /**
     * 将元组转成并行流
     *
     * @return 流
     */
    public Stream<T> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * 返回数组迭代器
     *
     * @return {@code Iterator<T>}
     */
    @Override
    public Iterator<T> iterator() {
        return AnyIter.ofArray(true, values);
    }

    /**
     * 返回数组拷贝迭代器，这也意味着该迭代器线程安全
     *
     * @return {@code Iterator<T>}
     */
    public Iterator<T> copyIterator() {
        return AnyIter.ofArray(true, values).copyIterator();
    }

    @Override
    public int hashCode() {
        if (0 != this.hashCode) {
            return this.hashCode;
        }
        int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(values);
        this.hashCode = result;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple<?> that)) return false;
        return Arrays.deepEquals(values, that.values);
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }
}
