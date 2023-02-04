package pxf.tl.api;

import java.io.Serial;
import java.io.Serializable;

/**
 * 可变{@code Object}
 *
 * @param <T> 可变的类型
 * @author potatoxf
 */
public class MutableObject<T> implements Mutable<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private volatile T value;

    /**
     * 构造，空值
     */
    public MutableObject() {
    }

    /**
     * 构造
     *
     * @param value 值
     */
    public MutableObject(final T value) {
        this.value = value;
    }

    /**
     * 构建MutableObj
     *
     * @param value 被包装的值
     * @param <T>   值类型
     * @return MutableObj
     */
    public static <T> MutableObject<T> of(T value) {
        return new MutableObject<>(value);
    }

    // -----------------------------------------------------------------------
    @Override
    public T get() {
        return this.value;
    }

    @Override
    public void set(final T value) {
        this.value = value;
    }

    // -----------------------------------------------------------------------
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() == obj.getClass()) {
            final MutableObject<?> that = (MutableObject<?>) obj;
            return this.value.equals(that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }

    // -----------------------------------------------------------------------
    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }
}
