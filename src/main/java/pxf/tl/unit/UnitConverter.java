package pxf.tl.unit;


import pxf.tl.api.GetterForNumber;

/**
 * 单位转换器
 *
 * @author potatoxf
 */
public interface UnitConverter<E extends UnitInfo<E>> {

    /**
     * 精确转换
     *
     * @param value      值
     * @param originUnit 原单位
     * @param targetUnit 目标单位
     * @return 返回目标单位的值
     */
    GetterForNumber exactConvert(Number value, E originUnit, E targetUnit);

    /**
     * 人类易懂转换
     *
     * @param value      值
     * @param originUnit 原单位
     * @param targetUnit 目标单位
     * @return 返回目标单位的值
     */
    GetterForNumber humanConvert(Number value, E originUnit, E targetUnit);
}
