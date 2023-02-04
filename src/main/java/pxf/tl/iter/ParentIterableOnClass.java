package pxf.tl.iter;

/**
 * @author potatoxf
 */
public final class ParentIterableOnClass extends ParentIterable<Class<?>> {
    public ParentIterableOnClass(Class<?> start) {
        super(
                start,
                AnyIter.class,
                Class::getSuperclass,
                (c, e) -> c != null && c != AnyIter.class);
    }
}
