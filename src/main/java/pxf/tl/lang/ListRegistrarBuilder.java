package pxf.tl.lang;


import pxf.tl.function.FunctionThrow;
import pxf.tl.function.SupplierThrow;

import java.util.List;

/**
 * 链表注册器构建器
 *
 * @author potatoxf
 */
public class ListRegistrarBuilder<T, R> extends ListRegistrar<T> {
    private final FunctionThrow<List<T>, R, RuntimeException> factory;

    /**
     * @param factory
     */
    public ListRegistrarBuilder(FunctionThrow<List<T>, R, RuntimeException> factory) {
        this.factory = factory;
    }

    /**
     * @param supplierThrow
     * @param factory
     */
    public ListRegistrarBuilder(
            SupplierThrow<List<T>, RuntimeException> supplierThrow,
            FunctionThrow<List<T>, R, RuntimeException> factory) {
        super(supplierThrow);
        this.factory = factory;
    }

    /**
     * @param t
     * @return
     */
    @Override
    public ListRegistrarBuilder<T, R> register(T t) {
        super.register(t);
        return this;
    }

    /**
     * @return
     */
    public R build() {
        return factory.apply(getList());
    }
}
