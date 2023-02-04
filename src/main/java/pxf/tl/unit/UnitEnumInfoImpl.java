package pxf.tl.unit;

/**
 * 单位枚举实现类
 *
 * @author potatoxf
 */
public interface UnitEnumInfoImpl<E extends Enum<E> & UnitEnumInfoImpl<E>>
        extends UnitInfoImpl<E>, UnitEnumInfo<E> {
}
