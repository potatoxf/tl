package pxf.tl.unit;

/**
 * 单位枚举信息
 *
 * @author potatoxf
 */
public interface UnitEnumInfo<E extends Enum<E> & UnitEnumInfo<E>> extends UnitInfo<E> {
    /**
     * 持有 {@code GeneralUnitInfo<E> }
     *
     * @return {@code GeneralUnitInfo<E>}
     */
    GeneralUnitInfo<E> holdUnitInfo();

    /**
     * 该单位与下一个大单位的间隔，等级是越来越高
     *
     * @return 与下一个大单位间隔数值
     */
    @Override
    default int nextIntervalExact() {
        return holdUnitInfo().nextIntervalExact();
    }

    /**
     * 该单位与下一个大单位的间隔，等级是越来越高
     *
     * @return 与下一个大单位间隔数值
     */
    @Override
    default int nextIntervalHuman() {
        return holdUnitInfo().nextIntervalHuman();
    }

    /**
     * 获取等级，从小到大依次增加，每相差一就代表两个单位相差一级
     *
     * @return 返回数字等级
     */
    @Override
    default int grade() {
        return holdUnitInfo().grade();
    }

    /**
     * 单位分类
     *
     * @return {@code UnitCatalog<E>}
     */
    @Override
    default UnitCatalog<E> unitCatalog() {
        return holdUnitInfo().unitCatalog();
    }
}
