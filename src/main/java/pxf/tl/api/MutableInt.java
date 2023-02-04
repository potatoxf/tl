package pxf.tl.api;


import java.io.Serial;
import java.util.Objects;

/**
 * 可变 <code>int</code> 类型
 *
 * @see Integer
 * @author potatoxf
 */
public class MutableInt extends Number implements Comparable<MutableInt>, Mutable<Number> {
    @Serial
    private static final long serialVersionUID = 1L;

    private volatile int value;

    /**
     * 构造，默认值0
     */
    public MutableInt() {
    }

    /**
     * 构造
     *
     * @param value 值
     */
    public MutableInt(final int value) {
        this.value = value;
    }

    /**
     * 构造
     *
     * @param value 值
     */
    public MutableInt(final Number value) {
        this(value.intValue());
    }

    /**
     * 构造
     *
     * @param value String值
     * @throws NumberFormatException 数字转换错误
     */
    public MutableInt(final String value) throws NumberFormatException {
        this.value = Integer.parseInt(value);
    }

    @Override
    public Integer get() {
        return this.value;
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void set(final int value) {
        this.value = value;
    }

    @Override
    public void set(final Number value) {
        this.value = value.intValue();
    }

    // -----------------------------------------------------------------------

    /**
     * 值+1
     *
     * @return this
     */
    public MutableInt increment() {
        value++;
        return this;
    }

    /**
     * 值减一
     *
     * @return this
     */
    public MutableInt decrement() {
        value--;
        return this;
    }

    // -----------------------------------------------------------------------

    /**
     * 增加值
     *
     * @param operand 被增加的值
     * @return this
     */
    public MutableInt add(final int operand) {
        this.value += operand;
        return this;
    }

    /**
     * 增加值
     *
     * @param operand 被增加的值，非空
     * @return this
     * @throws NullPointerException if the object is null
     */
    public MutableInt add(final Number operand) {
        this.value += operand.intValue();
        return this;
    }

    /**
     * 减去值
     *
     * @param operand 被减的值
     * @return this
     */
    public MutableInt subtract(final int operand) {
        this.value -= operand;
        return this;
    }

    /**
     * 减去值
     *
     * @param operand 被减的值，非空
     * @return this
     * @throws NullPointerException if the object is null
     */
    public MutableInt subtract(final Number operand) {
        this.value -= operand.intValue();
        return this;
    }

    // -----------------------------------------------------------------------
    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    // -----------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MutableInt that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }


    // -----------------------------------------------------------------------

    /**
     * 比较
     *
     * @param other 其它 MutableInt 对象
     * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
     */
    @Override
    public int compareTo(final MutableInt other) {
        return Integer.compare(this.value, other.value);
    }

    // -----------------------------------------------------------------------
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
