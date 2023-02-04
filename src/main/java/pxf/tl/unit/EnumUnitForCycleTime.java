package pxf.tl.unit;

/**
 * 时间周期转换
 *
 * @author potatoxf
 */
public enum EnumUnitForCycleTime implements UnitEnumInfo<EnumUnitForCycleTime> {
    /**
     * 月
     */
    MONTH(12),
    /**
     * 年
     */
    YEAR(-1);
    private final GeneralUnitInfo<EnumUnitForCycleTime> unitInfo;

    EnumUnitForCycleTime(int next) {
        unitInfo = new GeneralUnitInfo<>(EnumUnitForCycleTime.class, ordinal(), next);
    }

    /**
     * 持有 {@code GeneralUnitInfo<E> }
     *
     * @return {@code GeneralUnitInfo<E>}
     */
    @Override
    public GeneralUnitInfo<EnumUnitForCycleTime> holdUnitInfo() {
        return unitInfo;
    }
}
