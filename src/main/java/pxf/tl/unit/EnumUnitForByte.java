package pxf.tl.unit;

/**
 * 字节单位
 *
 * @author potatoxf
 */
public enum EnumUnitForByte implements UnitEnumInfo<EnumUnitForByte> {
    /**
     * 字节
     */
    B,
    /**
     * 千字节
     */
    KB,
    /**
     * 兆字节
     */
    MB,
    /**
     * GB
     */
    GB,
    /**
     * TB
     */
    TB,
    /**
     * PB
     */
    PB;
    private final GeneralUnitInfo<EnumUnitForByte> unitInfo;

    EnumUnitForByte() {
        unitInfo = new GeneralUnitInfo<>(EnumUnitForByte.class, ordinal(), 1024, 1000);
    }

    /**
     * 持有 {@code GeneralUnitInfo<E> }
     *
     * @return {@code GeneralUnitInfo<E>}
     */
    @Override
    public GeneralUnitInfo<EnumUnitForByte> holdUnitInfo() {
        return unitInfo;
    }
}
