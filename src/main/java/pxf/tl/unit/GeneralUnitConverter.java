package pxf.tl.unit;


import pxf.tl.api.GetterForNumber;
import pxf.tl.lang.AbstractDigital;
import pxf.tl.lang.DivideContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 通过单位转换器
 *
 * @author potatoxf
 */
public final class GeneralUnitConverter<E extends UnitInfo<E>> implements UnitConverter<E> {

    private static final Map<DivideContext, GeneralUnitConverter<?>> CACHE = new HashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();
    private final DivideContext divideContext;

    private GeneralUnitConverter(DivideContext divideContext) {
        this.divideContext = divideContext;
    }

    @SuppressWarnings("unchecked")
    public static <E extends UnitInfo<E>> GeneralUnitConverter<E> of(DivideContext divideContext) {
        if (divideContext == null) {
            throw new IllegalArgumentException("The DivideContext must be no null");
        }
        LOCK.lock();
        try {
            GeneralUnitConverter<E> generalUnitConverter =
                    (GeneralUnitConverter<E>) CACHE.get(divideContext);
            if (generalUnitConverter == null) {
                generalUnitConverter = new GeneralUnitConverter<>(divideContext);
                CACHE.put(divideContext, generalUnitConverter);
            }
            return generalUnitConverter;
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 精确转换
     *
     * @param value      值
     * @param originUnit 原单位
     * @param targetUnit 目标单位
     * @return 返回目标单位的值
     */
    @Override
    public GetterForNumber exactConvert(Number value, E originUnit, E targetUnit) {
        int diff = originUnit.poorGrade(targetUnit);
        int interval = originUnit.exactFactor(targetUnit);
        return calculateResult(value, diff, interval);
    }

    /**
     * 人类易懂转换
     *
     * @param value      值
     * @param originUnit 原单位
     * @param targetUnit 目标单位
     * @return 返回目标单位的值
     */
    @Override
    public GetterForNumber humanConvert(Number value, E originUnit, E targetUnit) {
        int diff = originUnit.poorGrade(targetUnit);
        int interval = originUnit.humanFactor(targetUnit);
        return calculateResult(value, diff, interval);
    }

    private GetterForNumber calculateResult(Number value, int diff, int interval) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value.doubleValue());
        if (diff < 0) {
            return new Digital(
                    bigDecimal.divide(
                            BigDecimal.valueOf(interval),
                            divideContext.getScale(),
                            divideContext.getRoundingMode()));
        } else if (diff > 0) {
            return new Digital(bigDecimal.multiply(BigDecimal.valueOf(interval)));
        }
        return new Digital(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneralUnitConverter<?> that = (GeneralUnitConverter<?>) o;
        return divideContext.equals(that.divideContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(divideContext);
    }

    private static class Digital extends AbstractDigital {
        private final Number number;

        private Digital(Number number) {
            this.number = number;
        }

        /**
         * 获取数字对象
         *
         * @param defaultValue 默认之
         * @return {@code Number}
         */
        @Override
        protected Number parseNumber(Number defaultValue) {
            return number == null ? defaultValue : number;
        }

        /**
         * 获取数字对象
         *
         * @return {@code Number}
         */
        @Override
        protected Number configDefaultNumber() {
            return 0;
        }
    }
}
