package pxf.tl.unit;

/**
 * 数字进位
 *
 * @author potatoxf
 */
public enum EnumUnitForNumber implements UnitEnumInfo<EnumUnitForNumber> {
    /**
     * 万分比
     */
    TEN_THOUSANDTHS(10),
    /**
     * 千分比
     */
    THOUSANDS(10),
    /**
     * 百分比
     */
    PERCENTAGE(100),
    /**
     * 正常
     */
    NORMAL(-1);
    private final GeneralUnitInfo<EnumUnitForNumber> unitInfo;

    EnumUnitForNumber(int next) {
        unitInfo = new GeneralUnitInfo<>(EnumUnitForNumber.class, ordinal(), next);
    }

    /**
     * 持有 {@code GeneralUnitInfo<E> }
     *
     * @return {@code GeneralUnitInfo<E>}
     */
    @Override
    public GeneralUnitInfo<EnumUnitForNumber> holdUnitInfo() {
        return unitInfo;
    }
}
