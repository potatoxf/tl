package pxf.tl.lang;

import pxf.tl.function.SupplierThrow;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 链表注册器
 *
 * @author potatoxf
 */
public class ListRegistrar<T> {

    private final List<T> chain;

    public ListRegistrar() {
        this.chain = new LinkedList<T>();
    }

    public ListRegistrar(SupplierThrow<List<T>, RuntimeException> supplierThrow) {
        this.chain = supplierThrow.get();
    }

    public ListRegistrar<T> register(T t) {
        if (t != null) {
            chain.add(t);
        }
        return this;
    }

    /**
     * 排序
     *
     * @param comparator 比较器
     * @return {@code this}
     */
    public ListRegistrar<T> sort(Comparator<? super T> comparator) {
        chain.sort(comparator);
        return this;
    }

    public List<T> getList(Comparator<? super T> comparator) {
        sort(comparator);
        return Collections.unmodifiableList(chain);
    }

    public List<T> getList() {
        return Collections.unmodifiableList(chain);
    }
}
