package pxf.tl.unit;


import pxf.tl.api.GetterForNumber;
import pxf.tl.lang.DivideContext;

/**
 * 单位实现类
 *
 * @author potatoxf
 */
public interface UnitInfoImpl<E extends UnitInfoImpl<E>> extends UnitInfo<E> {

    /**
     * 除法环境
     *
     * @return 返回 {@code DivideContext}
     */
    default DivideContext divideContext() {
        return DivideContext.DEFAULT;
    }

    /**
     * 持有 {@code UnitConverter<E>}
     *
     * @return {@code UnitConverter<E>}
     */
    default UnitConverter<E> holdUnitConverter() {
        return GeneralUnitConverter.of(divideContext());
    }

    /**
     * 精确转换
     *
     * @param value      值
     * @param targetUnit 目标单位
     * @return 返回目标单位的值
     */
    default GetterForNumber exactConvert(Number value, E targetUnit) {
        return holdUnitConverter().exactConvert(value, this$(), targetUnit);
    }

    /**
     * 人类易懂转换
     *
     * @param value      值
     * @param targetUnit 目标单位
     * @return 返回目标单位的值
     */
    default GetterForNumber humanConvert(Number value, E targetUnit) {
        return holdUnitConverter().humanConvert(value, this$(), targetUnit);
    }
}
