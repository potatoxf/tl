package pxf.tl.unit;

/**
 * @author potatoxf
 */
public interface UnitCatalog<E extends UnitInfo<E>> extends Iterable<E> {

    /**
     * 目标类型
     *
     * @return {@code Class<E>}
     */
    Class<E> targetType();
}
