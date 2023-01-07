package pxf.tl.api;

import java.util.Objects;

/**
 * 带有数值的对象包装
 *
 * @param <T> 对象类型
 * @author potatoxf
 */
public class MutableObjectNumber<T> extends MutableObject<T> implements GetterForNumber {
    protected volatile Number numberValue;

    public MutableObjectNumber(T value, Number numberValue) {
        super(value);
        this.numberValue = numberValue;
    }

    @Override
    public Number getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Number numberValue) {
        this.numberValue = numberValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MutableObjectNumber<?> that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(numberValue, that.numberValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numberValue);
    }

}
