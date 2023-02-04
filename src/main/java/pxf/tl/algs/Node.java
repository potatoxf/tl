package pxf.tl.algs;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 结点，数据结构对数据值的封装
 *
 * <p>该节结点是线程安全的
 *
 * @author potatoxf
 */
public abstract class Node<V, N extends Node<V, N>> {

    /**
     * 值
     */
    private final AtomicReference<V> value = new AtomicReference<>(null);
    /**
     * 是否可修改
     */
    private final boolean isCanModifyValue;

    /**
     * @param value 节点保存的值
     */
    public Node(V value) {
        this(value, true);
    }

    /**
     * @param value            节点保存的值
     * @param isCanModifyValue 是否可以修改
     */
    public Node(V value, boolean isCanModifyValue) {
        this.value.set(value);
        this.isCanModifyValue = isCanModifyValue;
    }

    /**
     * 返回当前值
     *
     * @return {@code V}当前值
     */
    public final V value() {
        return this.value.get();
    }

    /**
     * 更新值
     *
     * @param newValue 新的值
     * @return {@code V}原来的值
     */
    public final V updateValue(V newValue) {
        if (isCanModifyValue) {
            return value.getAndSet(newValue);
        } else {
            throw new UnsupportedOperationException("Modifying values is not supported");
        }
    }

    @Override
    public String toString() {
        return "Node{" + value + "}";
    }
}
