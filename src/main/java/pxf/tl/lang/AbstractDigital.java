package pxf.tl.lang;

import pxf.tl.api.GetterForNumber;

/**
 * 抽象数字
 *
 * @author potatoxf
 */
public abstract class AbstractDigital extends Number implements GetterForNumber {
    /**
     * Returns the value of the specified number as an <code>int</code>. This may involve rounding or
     * truncation.
     *
     * @return the numeric value represented by this object after conversion to type <code>int</code>.
     */
    @Override
    public final int intValue() {
        return gainIntValue(getDefaultNumber().intValue());
    }

    /**
     * Returns the value of the specified number as a <code>long</code>. This may involve rounding or
     * truncation.
     *
     * @return the numeric value represented by this object after conversion to type <code>long</code>
     * .
     */
    @Override
    public final long longValue() {
        return gainLongValue(getDefaultNumber().longValue());
    }

    /**
     * Returns the value of the specified number as a <code>float</code>. This may involve rounding.
     *
     * @return the numeric value represented by this object after conversion to type <code>float
     * </code>.
     */
    @Override
    public final float floatValue() {
        return gainFloatValue(getDefaultNumber().floatValue());
    }

    /**
     * Returns the value of the specified number as a <code>double</code>. This may involve rounding.
     *
     * @return the numeric value represented by this object after conversion to type <code>double
     * </code>.
     */
    @Override
    public final double doubleValue() {
        return gainDoubleValue(getDefaultNumber().doubleValue());
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    public Number getNumberValue() {
        return parseNumber(configDefaultNumber());
    }

    /**
     * 获取数字对象
     *
     * @param defaultValue 默认之
     * @return {@code Number}
     */
    protected abstract Number parseNumber(Number defaultValue);

    /**
     * 获取数字对象
     *
     * @return {@code Number}
     */
    protected abstract Number configDefaultNumber();

    /**
     * 获取数字对象
     *
     * @return {@code Number}
     */
    private Number getDefaultNumber() {
        Number number = configDefaultNumber();
        if (number == null) {
            throw new IllegalArgumentException();
        }
        if (number instanceof AbstractDigital) {
            throw new IllegalArgumentException();
        }
        return number;
    }
}
